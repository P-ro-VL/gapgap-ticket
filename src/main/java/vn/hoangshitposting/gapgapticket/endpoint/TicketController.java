package vn.hoangshitposting.gapgapticket.endpoint;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hoangshitposting.gapgapticket.dto.request.TicketHoldRequest;
import vn.hoangshitposting.gapgapticket.dto.request.TicketPurchaseRequest;
import vn.hoangshitposting.gapgapticket.model.TicketHoldModel;
import vn.hoangshitposting.gapgapticket.service.BuyingQueueService;
import vn.hoangshitposting.gapgapticket.service.TicketService;

import java.util.UUID;

@RestController
@RequestMapping("/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final BuyingQueueService queueService;

    /**
     * Enqueue a user (sessionId) into the buying queue.
     */
    @PostMapping("/queue/join")
    public ResponseEntity<String> joinQueue(@RequestParam String sessionId) {
        queueService.enqueueUser(sessionId);
        return ResponseEntity.ok("User enqueued");
    }

    /**
     * Check a user's position in the queue.
     */
    @GetMapping("/queue/position")
    public ResponseEntity<Long> checkQueuePosition(@RequestParam String sessionId) {
        long position = queueService.getPosition(sessionId);
        return ResponseEntity.ok(position);
    }

    /**
     * Dequeue the next user (should be protected/admin-only).
     */
    @PostMapping("/queue/next")
    public ResponseEntity<String> dequeueUser() {
        queueService.dequeueUser();
        return ResponseEntity.ok("User dequeued");
    }

    /**
     * Hold tickets for a user temporarily while paying.
     */
    @PostMapping("/hold")
    public ResponseEntity<UUID> holdTickets(@RequestBody TicketHoldRequest request) {
        UUID holdId = ticketService.holdTickets(request);
        return ResponseEntity.ok(holdId);
    }

    /**
     * Confirm ticket purchase using a valid hold ID.
     */
    @PostMapping("/purchase")
    public ResponseEntity<String> confirmPurchase(@RequestBody TicketPurchaseRequest request) {
        ticketService.confirmPurchase(request);
        return ResponseEntity.ok("Purchase confirmed");
    }

    /**
     * Get current hold info by hold ID (debug or UI use).
     */
    @GetMapping("/hold/{holdId}")
    public ResponseEntity<TicketHoldModel> getHoldInfo(@PathVariable String holdId) {
        TicketHoldModel hold = ticketService.getHoldInfo(holdId);
        return ResponseEntity.ok(hold);
    }

    /**
     * Cancel (release) a hold manually.
     */
    @DeleteMapping("/hold/{holdId}")
    public ResponseEntity<String> releaseHold(@PathVariable String holdId) {
        ticketService.releaseHold(holdId);
        return ResponseEntity.ok("Hold released");
    }
}
