package vn.hoangshitposting.gapgapticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vn.hoangshitposting.gapgapticket.model.TicketPurchaseModel;

import java.util.Optional;
import java.util.UUID;

@Service
public interface TicketPurchaseModelRepository extends JpaRepository<TicketPurchaseModel, UUID> {
    
    Optional<TicketPurchaseModel> findByCode(String code);

}
