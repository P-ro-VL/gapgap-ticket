package vn.hoangshitposting.gapgapticket.model;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.hoangshitposting.gapgapticket.model.converter.TimestampToLongConverter;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ticket_holds")
public class TicketHoldModel {
    @Id
    private UUID id;

    private UUID ticketId;

    private int quantity;

    @Convert(converter = TimestampToLongConverter.class)
    private long heldAt;

    private boolean confirmed;
}