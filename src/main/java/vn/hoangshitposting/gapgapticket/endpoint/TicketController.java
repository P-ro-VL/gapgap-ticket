package vn.hoangshitposting.gapgapticket.endpoint;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hoangshitposting.gapgapticket.api.ApiCallException;
import vn.hoangshitposting.gapgapticket.api.ApiCallResult;
import vn.hoangshitposting.gapgapticket.api.ApiExecutorService;
import vn.hoangshitposting.gapgapticket.api.ApiResponse;
import vn.hoangshitposting.gapgapticket.dto.request.TicketHoldRequest;
import vn.hoangshitposting.gapgapticket.dto.request.TicketPurchaseRequest;
import vn.hoangshitposting.gapgapticket.dto.request.UpdateHoldInfoRequest;
import vn.hoangshitposting.gapgapticket.dto.response.TicketHoldResponse;
import vn.hoangshitposting.gapgapticket.dto.response.TicketResponse;
import vn.hoangshitposting.gapgapticket.model.TicketHoldModel;
import vn.hoangshitposting.gapgapticket.service.BuyingQueueService;
import vn.hoangshitposting.gapgapticket.service.TicketService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final ApiExecutorService apiExecutorService;

    private final TicketService ticketService;
    private final BuyingQueueService queueService;

    /**
     * Enqueue a user (sessionId) into the buying queue.
     */
    @PostMapping("/queue/join")
    public ResponseEntity<ApiResponse<Boolean>> joinQueue(@RequestParam String sessionId, HttpServletRequest httpServletRequest) {
        queueService.enqueueUser(sessionId);
        return apiExecutorService.execute(httpServletRequest, () -> new ApiCallResult<>(true));
    }

    /**
     * Check a user's position in the queue.
     */
    @GetMapping("/queue/position")
    public ResponseEntity<ApiResponse<Long>> checkQueuePosition(@RequestParam String sessionId, HttpServletRequest httpServletRequest) {
        long position = queueService.getPosition(sessionId);
        return apiExecutorService.execute(httpServletRequest, () -> new ApiCallResult<>(position));
    }

    /**
     * Dequeue the next user (should be protected/admin-only).
     */
    @PostMapping("/queue/next")
    public ResponseEntity<ApiResponse<Boolean>> dequeueUser(HttpServletRequest httpServletRequest) {
        queueService.dequeueUser();
        return apiExecutorService.execute(httpServletRequest, () -> new ApiCallResult<>(true));
    }

    /**
     * Hold tickets for a user temporarily while paying.
     */
    @PostMapping("/hold")
    public ResponseEntity<ApiResponse<UUID>> holdTickets(@RequestBody TicketHoldRequest request, HttpServletRequest httpServletRequest) throws ApiCallException {
        UUID holdId = ticketService.holdTickets(request);
        return apiExecutorService.execute(httpServletRequest, () -> new ApiCallResult<>(holdId));
    }

    /**
     * Confirm ticket purchase using a valid hold ID.
     */
    @PostMapping("/purchase")
    public ResponseEntity<ApiResponse<Boolean>> confirmPurchase(@RequestBody TicketPurchaseRequest request, HttpServletRequest httpServletRequest) throws ApiCallException {
        ticketService.confirmPurchase(request);
        return apiExecutorService.execute(httpServletRequest, () -> new ApiCallResult<>(true));
    }

    /**
     * Get current hold info by hold ID (debug or UI use).
     */
    @GetMapping("/hold/{holdId}")
    public ResponseEntity<ApiResponse<TicketHoldResponse>> getHoldInfo(@PathVariable String holdId, HttpServletRequest httpServletRequest) {
        return apiExecutorService.execute(httpServletRequest, () -> new ApiCallResult<>(ticketService.getHoldInfo(holdId)));
    }

    /**
     * Get all ticket types
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getAllTickets(HttpServletRequest httpServletRequest) {
        return apiExecutorService.execute(httpServletRequest, () -> new ApiCallResult<>(ticketService.getAllTicketTypes()));
    }

    /**
     * Update hold info
     */
    @PutMapping("/hold/{holdId}")
    public ResponseEntity<ApiResponse<Boolean>> updateHold(@PathVariable String holdId, @RequestBody UpdateHoldInfoRequest body, HttpServletRequest httpServletRequest) throws ApiCallException {
        ticketService.updateHoldInfo(holdId, body.getAmount());
        return apiExecutorService.execute(httpServletRequest, () -> new ApiCallResult<>(true));
    }
}
