package pl.chopy.reserve_court_backend.model.entity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.chopy.reserve_court_backend.model.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
