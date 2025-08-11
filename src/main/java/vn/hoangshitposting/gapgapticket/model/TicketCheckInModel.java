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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ticket_check_in")
public class TicketCheckInModel {

    @Id
    UUID id;

    String code;

    @Convert(converter = TimestampToLongConverter.class)
    Long checkInAt;

}
