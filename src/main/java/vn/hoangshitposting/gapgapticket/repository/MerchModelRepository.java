package vn.hoangshitposting.gapgapticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.hoangshitposting.gapgapticket.model.merch.MerchModel;

import java.util.UUID;

@Repository
public interface MerchModelRepository extends JpaRepository<MerchModel, UUID> {
}
