package vn.hoangshitposting.gapgapticket.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MerchMetaRequest {

    String merchId;

    String name;

    int price;

    Map<String, String> metadata;

    int amount;

}
