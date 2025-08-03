package vn.hoangshitposting.gapgapticket.model.merch;

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
@Table(name = "merch_purchases")
public class MerchPurchaseModel {

    @Id
    UUID id;

    String email;

    String fullName;

    String phoneNumber;

    String address;

    String ward;

    String province;

    String shippingBrand;

    int shippingFee;

    String proof;

    @Convert(converter = TimestampToLongConverter.class)
    Long purchasedAt;

}
