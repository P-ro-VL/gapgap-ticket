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
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "gallery_invitations")
public class GalleryInvitationModel {

    @Id
    UUID id;

    String email;

    String name;

    @Convert(converter = TimestampToLongConverter.class)
    Long invitedAt;

}
