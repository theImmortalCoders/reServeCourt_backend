package pl.chopy.reserve_court_backend.model.entity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.chopy.reserve_court_backend.model.entity.Court;
import pl.chopy.reserve_court_backend.model.entity.Reservation;
import pl.chopy.reserve_court_backend.model.entity.User;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	List<Reservation> findAllByCourt(Court court);

	List<Reservation> findAllByBooker(User booker);
}
