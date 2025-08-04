package vn.hoangshitposting.gapgapticket.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.hoangshitposting.gapgapticket.model.converter.TimestampToLongConverter;

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

    @Column(name = "paymentInfo", length = Integer.MAX_VALUE)
    private String paymentInfo;

    @Column(name = "paymentInfo", length = Integer.MAX_VALUE)
    private String proof;

    private int quantity;

    @Convert(converter = TimestampToLongConverter.class)
    private long purchasedAt;

    private String code;
}
