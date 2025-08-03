package vn.hoangshitposting.gapgapticket.endpoint;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hoangshitposting.gapgapticket.api.ApiCallResult;
import vn.hoangshitposting.gapgapticket.api.ApiExecutorService;
import vn.hoangshitposting.gapgapticket.api.ApiResponse;
import vn.hoangshitposting.gapgapticket.dto.request.BuyMerchRequest;
import vn.hoangshitposting.gapgapticket.dto.response.MerchPaymentResponse;
import vn.hoangshitposting.gapgapticket.service.MerchService;

@RestController
@RequestMapping(path = "/v1/merch")
@AllArgsConstructor
public class MerchController {

    ApiExecutorService apiExecutorService;

    MerchService merchService;

    @PostMapping(path = "/buy")
    public ResponseEntity<ApiResponse<MerchPaymentResponse>> buyMerch(@RequestBody BuyMerchRequest body, HttpServletRequest httpServletRequest) {
        return apiExecutorService.execute(httpServletRequest, () -> new ApiCallResult<>(merchService.buyMerch(body)));
    }
}
