package vn.hoangshitposting.gapgapticket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponse {

    private UUID id;

    private String name;

    private long openTime;

    private boolean soldOut;

    private int maxTicketHold;

    private int price;

}
