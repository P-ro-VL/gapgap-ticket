package vn.hoangshitposting.gapgapticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.hoangshitposting.gapgapticket.model.TicketModel;

import java.util.UUID;

@Repository
public interface TicketModelRepository extends JpaRepository<TicketModel, UUID> {

}
