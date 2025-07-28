package vn.hoangshitposting.gapgapticket.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.hoangshitposting.gapgapticket.model.TicketModel;
import vn.hoangshitposting.gapgapticket.model.TicketPurchaseModel;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendEmailRequest {

    String name;

    TicketModel ticket;

    int amount;

    List<TicketPurchaseModel> purchases = new ArrayList<>();

    TicketPurchaseRequest purchaseRequest;
}
