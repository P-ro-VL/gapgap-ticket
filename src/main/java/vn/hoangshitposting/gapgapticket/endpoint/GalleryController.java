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
import vn.hoangshitposting.gapgapticket.dto.request.GalleryInvitationRequest;
import vn.hoangshitposting.gapgapticket.service.GalleryService;

@RequestMapping(path = "/v1/gallery")
@RestController
@AllArgsConstructor
public class GalleryController {

    ApiExecutorService apiExecutorService;

    GalleryService galleryService;

    @PostMapping(path = "/invite")
    public ResponseEntity<ApiResponse<Object>> inviteToGallery(@RequestBody GalleryInvitationRequest request, HttpServletRequest httpServletRequest) {
        return apiExecutorService.execute(httpServletRequest, () -> new ApiCallResult<>(galleryService.sendGalleryInvitationEmail(request)));
    }

}
