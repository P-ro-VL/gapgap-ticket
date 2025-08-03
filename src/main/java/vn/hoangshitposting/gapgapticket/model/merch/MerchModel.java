package vn.hoangshitposting.gapgapticket.model.merch;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.hoangshitposting.gapgapticket.model.converter.MerchMetaConverter;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "merches")
public class MerchModel {

    @Id
    UUID id;

    String name;

    int price;

    @Convert(converter = MerchMetaConverter.class)
    MerchMeta metadata;

}
