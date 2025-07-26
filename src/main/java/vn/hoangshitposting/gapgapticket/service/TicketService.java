package vn.hoangshitposting.gapgapticket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hoangshitposting.gapgapticket.dto.request.TicketHoldRequest;
import vn.hoangshitposting.gapgapticket.dto.request.TicketPurchaseRequest;
import vn.hoangshitposting.gapgapticket.model.TicketHoldModel;
import vn.hoangshitposting.gapgapticket.model.TicketModel;
import vn.hoangshitposting.gapgapticket.model.TicketPurchaseModel;
import vn.hoangshitposting.gapgapticket.repository.TicketModelRepository;
import vn.hoangshitposting.gapgapticket.repository.TicketPurchaseModelRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketModelRepository ticketModelRepository;
    private final TicketPurchaseModelRepository ticketPurchaseModelRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String HOLD_PREFIX = "ticket:hold:";
    private static final Duration HOLD_DURATION = Duration.ofMinutes(5);

    public UUID holdTickets(TicketHoldRequest request) {
        if (request.getQuantity() < 1 || request.getQuantity() > 3) {
            throw new IllegalArgumentException("Can only hold between 1 and 3 tickets");
        }

        TicketModel ticketType = ticketModelRepository.findById(request.getTicketId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid ticket type"));

        LocalDateTime openTime = LocalDateTime.ofInstant(new Date(ticketType.getOpenTime()).toInstant(), ZoneId.systemDefault());
        if (LocalDateTime.now().isBefore(openTime)) {
            throw new IllegalStateException("Ticket type not yet available");
        }

        synchronized (ticketType) {
            if (ticketType.getRemainingQuantity() < request.getQuantity()) {
                throw new IllegalStateException("Not enough tickets available");
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
        redisTemplate.opsForValue().set(HOLD_PREFIX + holdId, holdInfo, HOLD_DURATION);
        return holdId;
    }

    @Transactional
    public void confirmPurchase(TicketPurchaseRequest request) {
        String redisKey = HOLD_PREFIX + request.getHoldId();
        TicketHoldModel holdInfo = (TicketHoldModel) redisTemplate.opsForValue().get(redisKey);

        if (holdInfo == null) {
            throw new IllegalStateException("Hold expired or not found");
        }

        // Persist the purchase
        ticketPurchaseModelRepository.save(
                TicketPurchaseModel.builder()
                        .id(UUID.randomUUID())
                        .purchasedAt(System.currentTimeMillis())
                        .paymentInfo(request.getPaymentInfo())
                        .quantity(holdInfo.getQuantity())
                        .ticketId(holdInfo.getTicketId())
                        .build()
        );

        // Clean up the hold
        redisTemplate.delete(redisKey);
    }

    public TicketHoldModel getHoldInfo(String holdId) {
        String key = HOLD_PREFIX + holdId;
        return (TicketHoldModel) redisTemplate.opsForValue().get(key);
    }

    public void releaseHold(String holdId) {
        redisTemplate.delete(HOLD_PREFIX + holdId);
    }

    public boolean isHoldValid(String holdId) {
        return redisTemplate.hasKey(HOLD_PREFIX + holdId);
    }
}
