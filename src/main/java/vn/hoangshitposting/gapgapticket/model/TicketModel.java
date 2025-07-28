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
@Table(name = "tickets")
public class TicketModel {

    @Id
    private UUID id;

    private String name;

    @Convert(converter = TimestampToLongConverter.class)
    private long openTime;

    private int totalQuantity;

    private int remainingQuantity;

    private int maxPerHold;

    private int price;

}
