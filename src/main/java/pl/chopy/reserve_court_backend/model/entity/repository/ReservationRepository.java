package pl.chopy.reserve_court_backend.model.entity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.chopy.reserve_court_backend.model.entity.Club;
import pl.chopy.reserve_court_backend.model.entity.Court;
import pl.chopy.reserve_court_backend.model.entity.Reservation;
import pl.chopy.reserve_court_backend.model.entity.User;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	List<Reservation> findAllByCourt(Court court);

	List<Reservation> findAllByBooker(User booker);

	@Query("SELECT Reservation r FROM Reservation " +
			"WHERE r.court.club.id = ?1")
	List<Reservation> findAllByClub(Club club);
}
