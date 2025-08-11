package vn.hoangshitposting.gapgapticket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketCheckInResponse {

    UUID purchaseId;

    UUID checkInId;

    TicketResponse ticket;

    int quantity;

    String name;

    String email;

    String phoneNumber;

    String code;
}
