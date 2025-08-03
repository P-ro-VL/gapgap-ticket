package vn.hoangshitposting.gapgapticket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.hoangshitposting.gapgapticket.dto.request.GalleryInvitationRequest;
import vn.hoangshitposting.gapgapticket.model.GalleryInvitationModel;
import vn.hoangshitposting.gapgapticket.repository.GalleryInvitationModelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GalleryService {

    final GalleryInvitationModelRepository galleryInvitationModelRepository;
    final GoogleSheetService googleSheetService;

    public boolean sendGalleryInvitationEmail(GalleryInvitationRequest request) {
        UUID id = UUID.randomUUID();
        long now = System.currentTimeMillis();

        GalleryInvitationModel invitationModel = GalleryInvitationModel.builder()
                .id(id)
                .email(request.getEmail())
                .name(request.getFullName())
                .invitedAt(now)
                .build();
        galleryInvitationModelRepository.save(invitationModel);

        new Thread(() -> {
            try {
                EmailService.sendGalleryInvitationEmail(request.getEmail(), request);

                googleSheetService.appendRow(
                        GoogleSheetService.SpreadSheet.GALLERY,
                        new ArrayList<>(List.of(
                                request.getEmail(),
                                request.getFullName(),
                                now
                        ))
                );
            } catch (Exception ex) {
                System.out.println("CANNOT RECORD GALLERY INVITATION TO GOOGLE SHEET");
                System.out.println(request.getEmail() + " - " + request.getFullName() + " - " + now);
            }
        }).start();

        return true;
    }

}
