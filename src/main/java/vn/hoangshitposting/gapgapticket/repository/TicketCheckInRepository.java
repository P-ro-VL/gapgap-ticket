package vn.hoangshitposting.gapgapticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.hoangshitposting.gapgapticket.model.TicketCheckInModel;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketCheckInRepository extends JpaRepository<TicketCheckInModel, UUID> {

    Optional<TicketCheckInModel> findByCode(String code);

}
