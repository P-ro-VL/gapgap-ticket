package vn.hoangshitposting.gapgapticket.dto.request;

import jakarta.persistence.Convert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.hoangshitposting.gapgapticket.model.converter.TimestampToLongConverter;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyMerchRequest {

    String email;

    String fullName;

    String phoneNumber;

    String address;

    String ward;

    String province;

    String shippingBrand;

    int shippingFee;

    String proof;

    List<MerchMetaRequest> merches;

}
