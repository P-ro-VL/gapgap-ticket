package vn.hoangshitposting.gapgapticket.endpoint;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hoangshitposting.gapgapticket.api.ApiCallResult;
import vn.hoangshitposting.gapgapticket.api.ApiExecutorService;
import vn.hoangshitposting.gapgapticket.api.ApiResponse;
import vn.hoangshitposting.gapgapticket.dto.request.ShippingRequest;
import vn.hoangshitposting.gapgapticket.service.ShippingService;

import java.util.Map;

@RestController
@RequestMapping("/v1/shipping")
@RequiredArgsConstructor
public class ShippingController {

    private final ApiExecutorService apiExecutorService;

    private final ShippingService shippingService;

    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> calculateShippingFee(@RequestBody ShippingRequest request, HttpServletRequest httpServletRequest) {
        return apiExecutorService.execute(httpServletRequest, () -> new ApiCallResult<>(shippingService.calculateShippingFee(
                request.getReceiverAddress(),
                request.getProductPrice()
        )));
    }
}
