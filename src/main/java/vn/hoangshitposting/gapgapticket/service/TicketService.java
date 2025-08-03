package vn.hoangshitposting.gapgapticket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hoangshitposting.gapgapticket.api.ApiCallException;
import vn.hoangshitposting.gapgapticket.dto.request.SendEmailRequest;
import vn.hoangshitposting.gapgapticket.dto.request.TicketHoldRequest;
import vn.hoangshitposting.gapgapticket.dto.request.TicketPurchaseRequest;
import vn.hoangshitposting.gapgapticket.dto.response.TicketHoldResponse;
import vn.hoangshitposting.gapgapticket.dto.response.TicketResponse;
import vn.hoangshitposting.gapgapticket.model.TicketHoldModel;
import vn.hoangshitposting.gapgapticket.model.TicketModel;
import vn.hoangshitposting.gapgapticket.model.TicketPurchaseModel;
import vn.hoangshitposting.gapgapticket.repository.TicketHoldModelRepository;
import vn.hoangshitposting.gapgapticket.repository.TicketModelRepository;
import vn.hoangshitposting.gapgapticket.repository.TicketPurchaseModelRepository;

import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketModelRepository ticketModelRepository;
    private final TicketPurchaseModelRepository ticketPurchaseModelRepository;

    private static final String HOLD_PREFIX = "ticket:hold:";
    private static final Duration HOLD_DURATION = Duration.ofMinutes(10);

    private static final SimpleDateFormat CODE_TIME_FORMAT = new SimpleDateFormat("HHmmssddMMyyyy");

    private final TicketHoldModelRepository ticketHoldModelRepository;
    private final GoogleSheetService googleSheetService;

    public UUID holdTickets(TicketHoldRequest request) throws ApiCallException {
        TicketModel ticketType = ticketModelRepository.findById(request.getTicketId())
                .orElseThrow(() -> new ApiCallException("Invalid ticket type", HttpStatus.BAD_REQUEST));

        if (request.getQuantity() < 1 || (ticketType.getMaxPerHold() != -1 && request.getQuantity() > ticketType.getMaxPerHold() )) {
            throw new ApiCallException("Chỉ có thể mua tối đa " + ticketType.getMaxPerHold() + " vé đối với vé " + ticketType.getName(), HttpStatus.BAD_REQUEST);
        }

        ZoneId zoneId = ZoneId.of("Asia/Bangkok"); // GMT+7
        LocalDateTime openTime = Instant.ofEpochMilli(ticketType.getOpenTime())
                .atZone(zoneId)
                .toLocalDateTime();

        if (LocalDateTime.now(zoneId).isBefore(openTime)) {
            throw new ApiCallException("Vé chưa mở bán hoặc đã kết thúc đợt bán vé", HttpStatus.BAD_REQUEST);
        }

        synchronized (ticketType) {
            boolean isSoldOut = ticketType.getRemainingQuantity() == 0;
            if(isSoldOut) throw new ApiCallException("Đã bán hết vé.", HttpStatus.BAD_REQUEST);

            if(request.getQuantity() > ticketType.getRemainingQuantity()) {
                throw new ApiCallException("Không còn đủ " + request.getQuantity() + " vé để mua. Vui lòng giảm số lượng.", HttpStatus.BAD_REQUEST);
            }

            ticketType.setRemainingQuantity(ticketType.getRemainingQuantity() - request.getQuantity());
            ticketModelRepository.save(ticketType);
        }

        UUID holdId = UUID.randomUUID();
        TicketHoldModel holdInfo = TicketHoldModel.builder()
                .id(holdId)
                .ticketId(request.getTicketId())
                .quantity(request.getQuantity())
                .heldAt(System.currentTimeMillis())
                .confirmed(false)
                .build();
        ticketHoldModelRepository.save(holdInfo);

        return holdId;
    }

    public void updateHoldInfo(String holdId, int newQuantity) throws ApiCallException {
        Optional<TicketHoldModel> holdInfoOpt = ticketHoldModelRepository.findById(UUID.fromString(holdId));
        if (holdInfoOpt.isEmpty()) {
            throw new ApiCallException("Không tìm thấy thông tin giữ vé", HttpStatus.NOT_FOUND);
        }

        TicketHoldModel holdInfo = holdInfoOpt.get();

        // Validate if new quantity is allowed
        Optional<TicketModel> ticketOpt = ticketModelRepository.findById(holdInfo.getTicketId());
        if (ticketOpt.isEmpty()) throw new ApiCallException("Vé không hợp lệ", HttpStatus.NOT_FOUND);
        TicketModel ticket = ticketOpt.get();

        if ((ticket.getMaxPerHold() != -1 && newQuantity > ticket.getMaxPerHold())) {
            throw new ApiCallException("Bạn chỉ được giữ tối đa " + ticket.getMaxPerHold() + " vé", HttpStatus.BAD_REQUEST);
        }

        boolean isSoldOut = ticket.getRemainingQuantity() == 0;
        if(isSoldOut) throw new ApiCallException("Đã bán hết vé.", HttpStatus.BAD_REQUEST);

        int diff = newQuantity - holdInfo.getQuantity();
        if (diff > 0 && diff > ticket.getRemainingQuantity()) {
            throw new ApiCallException("Không còn đủ " + newQuantity + " vé", HttpStatus.BAD_REQUEST);
        }

        // Update DB quantity if needed
        synchronized (ticket) {
            ticket.setRemainingQuantity(ticket.getRemainingQuantity() - diff);
            ticketModelRepository.save(ticket);
        }

        // Update hold info
        holdInfo.setQuantity(newQuantity);
        ticketHoldModelRepository.save(holdInfo);
    }

    @Transactional
    public void confirmPurchase(TicketPurchaseRequest request) throws ApiCallException {
        String redisKey = HOLD_PREFIX + request.getHoldId();
        Optional<TicketHoldModel> holdInfoOpt = ticketHoldModelRepository.findById(request.getHoldId());
        if(holdInfoOpt.isEmpty())
            throw new ApiCallException("Thời gian giữ vé đã hết hoặc không hợp lệ", HttpStatus.INTERNAL_SERVER_ERROR);

        TicketHoldModel holdInfo = holdInfoOpt.get();

        Optional<TicketModel> ticketOpt = ticketModelRepository.findById(holdInfo.getTicketId());
        if (ticketOpt.isEmpty()) throw new ApiCallException("Vé không hợp lệ", HttpStatus.NOT_FOUND);
        TicketModel ticket = ticketOpt.get();

        SendEmailRequest sendEmailRequest = new SendEmailRequest();
        sendEmailRequest.setPurchaseRequest(request);
        sendEmailRequest.setAmount(holdInfo.getQuantity());
        sendEmailRequest.setTicket(ticket);

        String paymentInfo = String.join("{br}", List.of(
                request.getName(),
                request.getEmail(),
                request.getProof(),
                request.getPhoneNumber()
        ));

        long now = System.currentTimeMillis();
        for(int i = 1; i <= holdInfo.getQuantity(); i++) {
            String code = "SG" + CODE_TIME_FORMAT.format(now) + i;
            // Persist the purchase
            TicketPurchaseModel purchaseModel = ticketPurchaseModelRepository.save(
                    TicketPurchaseModel.builder()
                            .id(UUID.randomUUID())
                            .purchasedAt(now)
                            .paymentInfo(paymentInfo)
                            .quantity(1)
                            .ticketId(holdInfo.getTicketId())
                            .code(code)
                            .build()
            );

            sendEmailRequest.getPurchases().add(purchaseModel);
        }

        holdInfo.setConfirmed(true);
        ticketHoldModelRepository.save(holdInfo);

        new Thread(() -> {
            EmailService.sendConfirmTicketEmail(
                    request.getEmail(),
                    sendEmailRequest
            );

            try {
                googleSheetService.appendRow(
                        GoogleSheetService.SpreadSheet.TICKET,
                        new ArrayList<>(List.of(
                                request.getName(),
                                ticket.getName(),
                                holdInfo.getQuantity(),
                                ticket.getPrice() * holdInfo.getQuantity(),
                                request.getEmail(),
                                request.getPhoneNumber(),
                                now,
                                request.getProof()
                        ))
                );
            } catch (Exception ex) {
                System.out.println("CANNOT RECORD PURCHASE HISTORY TO GOOGLE SHEET");
                System.out.println(request.getName() + " - " + holdInfo.getQuantity() + " tickets - " + ticket.getName() + " - " + now);
            }
        }).start();
    }

    public TicketHoldResponse getHoldInfo(String holdId) throws ApiCallException {
        Optional<TicketHoldModel> ticketHoldModelOpt = ticketHoldModelRepository.findById(UUID.fromString(holdId));
        if(ticketHoldModelOpt.isEmpty()) throw new ApiCallException("Cannot find hold info", HttpStatus.NOT_FOUND);

        TicketHoldModel ticketHoldModel = ticketHoldModelOpt.get();

        Optional<TicketModel> ticketOpt = ticketModelRepository.findById(ticketHoldModel.getTicketId());
        if(ticketOpt.isEmpty()) throw new ApiCallException("Invalid ticket", HttpStatus.NOT_FOUND);

        TicketModel ticket = ticketOpt.get();

        return TicketHoldResponse.builder()
                .ticket(
                        TicketResponse.builder()
                                .id(ticket.getId())
                                .name(ticket.getName())
                                .openTime(ticket.getOpenTime())
                                .soldOut(ticket.getRemainingQuantity() == 0)
                                .maxTicketHold(ticket.getMaxPerHold())
                                .price(ticket.getPrice())
                                .build()
                )
                .id(ticketHoldModel.getId())
                .confirmed(ticketHoldModel.isConfirmed())
                .heldAt(ticketHoldModel.getHeldAt())
                .quantity(ticketHoldModel.getQuantity())
                .build();
    }

    public List<TicketResponse> getAllTicketTypes() {
        return ticketModelRepository.findAll().stream().map(
                ticket -> TicketResponse.builder()
                        .id(ticket.getId())
                        .name(ticket.getName())
                        .openTime(ticket.getOpenTime())
                        .soldOut(ticket.getRemainingQuantity() == 0)
                        .maxTicketHold(ticket.getMaxPerHold())
                        .price(ticket.getPrice())
                        .build()
        ).collect(Collectors.toList());
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.SECONDS)
    public void removeExpiredHoldTickets() {
        List<TicketHoldModel> holds = ticketHoldModelRepository.findAll();
        long now = System.currentTimeMillis();

        for(TicketHoldModel hold : holds) {
            if (hold != null && !hold.isConfirmed() && (now - hold.getHeldAt()) > HOLD_DURATION.toMillis()) {
                ticketModelRepository.findById(hold.getTicketId()).ifPresent(ticket -> {
                    ticket.setRemainingQuantity(ticket.getRemainingQuantity() + hold.getQuantity());
                    ticketModelRepository.save(ticket);
                });
                ticketHoldModelRepository.delete(hold);
            }
        }
    }

}
