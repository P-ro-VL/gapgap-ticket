package vn.hoangshitposting.gapgapticket.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ticket_purchases")
public class TicketPurchaseModel {
    @Id
    private UUID id;

    private UUID ticketId;

    private String paymentInfo;

    private int quantity;

    private long purchasedAt;
}
