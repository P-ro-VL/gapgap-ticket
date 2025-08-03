package vn.hoangshitposting.gapgapticket.dto.response;

import jakarta.persistence.Convert;
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
public class MerchPaymentResponse {

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
