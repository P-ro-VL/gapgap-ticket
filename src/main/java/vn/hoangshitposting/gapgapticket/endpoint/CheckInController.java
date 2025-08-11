package vn.hoangshitposting.gapgapticket.endpoint;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hoangshitposting.gapgapticket.api.ApiCallResult;
import vn.hoangshitposting.gapgapticket.api.ApiExecutorService;
import vn.hoangshitposting.gapgapticket.api.ApiResponse;
import vn.hoangshitposting.gapgapticket.dto.response.TicketCheckInResponse;
import vn.hoangshitposting.gapgapticket.service.TicketService;

@RestController
@RequestMapping(path = "/checkin")
@AllArgsConstructor
public class CheckInController {

    ApiExecutorService apiExecutorService;
    TicketService ticketService;

    @PostMapping(path = "/{id}")
    public ResponseEntity<ApiResponse<TicketCheckInResponse>> checkIn(@PathVariable String code, HttpServletRequest httpServletRequest) {
        return apiExecutorService.execute(httpServletRequest, () -> new ApiCallResult<>(ticketService.checkIn(code)));
    }

}
