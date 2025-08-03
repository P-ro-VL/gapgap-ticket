package vn.hoangshitposting.gapgapticket.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyMerchRequest {

    String email;

    String fullName;

    String phoneNumber;

    String address;

    int shippingFee;

    String proof;

    List<MerchMetaRequest> merches;

}
