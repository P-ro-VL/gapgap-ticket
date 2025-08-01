package vn.hoangshitposting.gapgapticket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketHoldResponse {

    private UUID id;

    private TicketResponse ticket;

    private int quantity;

    private long heldAt;

    private boolean confirmed;

}
