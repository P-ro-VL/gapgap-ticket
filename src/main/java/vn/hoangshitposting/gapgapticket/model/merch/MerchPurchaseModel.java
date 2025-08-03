package vn.hoangshitposting.gapgapticket.model.merch;

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
@Table(name = "merch_purchases")
public class MerchPurchaseModel {

    @Id
    UUID id;

    String email;

    String fullName;

    String phoneNumber;

    String address;

    int shippingFee;

    @Column(name = "proof", length = Integer.MAX_VALUE)
    String proof;

    @Convert(converter = TimestampToLongConverter.class)
    Long purchasedAt;

}
