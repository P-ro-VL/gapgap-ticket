package vn.hoangshitposting.gapgapticket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.hoangshitposting.gapgapticket.dto.request.BuyMerchRequest;
import vn.hoangshitposting.gapgapticket.dto.request.MerchMetaRequest;
import vn.hoangshitposting.gapgapticket.dto.response.MerchPaymentResponse;
import vn.hoangshitposting.gapgapticket.dto.response.MerchResponse;
import vn.hoangshitposting.gapgapticket.model.merch.MerchPurchaseModel;
import vn.hoangshitposting.gapgapticket.repository.MerchModelRepository;
import vn.hoangshitposting.gapgapticket.repository.MerchPurchaseModelRepository;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MerchService {

    final MerchModelRepository merchModelRepository;
    final MerchPurchaseModelRepository merchPurchaseModelRepository;

    final GoogleSheetService googleSheetService;

    public List<MerchResponse> getAllMerch() {
        return merchModelRepository.findAll().stream().map(
                merch -> MerchResponse.builder()
                        .id(merch.getId())
                        .name(merch.getName())
                        .price(merch.getPrice())
                        .color(merch.getMetadata().getColor())
                        .size(merch.getMetadata().getSize())
                        .build()
        ).collect(Collectors.toList());
    }

    public MerchPaymentResponse buyMerch(BuyMerchRequest request) {
        UUID id = UUID.randomUUID();
        long now = System.currentTimeMillis();

        MerchPurchaseModel model = MerchPurchaseModel.builder()
                .id(id)
                .email(request.getEmail())
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .shippingFee(request.getShippingFee())
                .proof(request.getProof())
                .purchasedAt(now)
                .build();
        merchPurchaseModelRepository.save(model);

        new Thread(() -> {
            EmailService.sendConfirmMerchEmail(request.getEmail(), request);

            try {
                for(MerchMetaRequest merch : request.getMerches()) {
                    googleSheetService.appendRow(
                            GoogleSheetService.SpreadSheet.MERCH,
                            List.of(
                                    request.getEmail(),
                                    request.getFullName(),
                                    request.getPhoneNumber(),
                                    request.getAddress(),
                                    request.getShippingFee(),
                                    merch.getName(),
                                    merch.getAmount(),
                                    merch.getMetadata().getOrDefault("color", ""),
                                    merch.getMetadata().getOrDefault("size", ""),
                                    merch.getPrice(),
                                    merch.getPrice() * merch.getAmount(),
                                    now
                            )
                    );
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        }).start();

        return MerchPaymentResponse.builder()
                .id(id)
                .email(request.getEmail())
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .shippingFee(request.getShippingFee())
                .proof(request.getProof())
                .purchasedAt(now)
                .build();
    }

}
