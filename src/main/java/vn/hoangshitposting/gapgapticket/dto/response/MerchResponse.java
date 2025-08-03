package vn.hoangshitposting.gapgapticket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MerchResponse {

    UUID id;

    String name;

    int price;

    List<String> size;

    List<String> color;

}
