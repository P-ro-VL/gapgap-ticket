package vn.hoangshitposting.gapgapticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.hoangshitposting.gapgapticket.model.merch.MerchPurchaseModel;

import java.util.UUID;

@Repository
public interface MerchPurchaseModelRepository extends JpaRepository<MerchPurchaseModel, UUID> {
}
