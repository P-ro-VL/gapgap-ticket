package vn.hoangshitposting.gapgapticket.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketPurchaseRequest {

    UUID holdId;

    String name;

    String email;

    String phoneNumber;

    String proof;

}
