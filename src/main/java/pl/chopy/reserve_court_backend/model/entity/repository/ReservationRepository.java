package pl.chopy.reserve_court_backend.model.entity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.chopy.reserve_court_backend.model.entity.Court;
import pl.chopy.reserve_court_backend.model.entity.Reservation;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	List<Reservation> findAllByCourt(Court court);

	@Query("SELECT r from Reservation r " +
			"WHERE (r.booker.id = ?1) " +
			"AND (cast(?2 as localdatetime) IS NULL OR r.timeFrom >= ?2) " +
			"AND (cast(?3 as localdatetime) IS NULL OR r.timeFrom <= ?3) " +
			"AND r.isCanceled = false ")
	List<Reservation> findAllByBookerWithFilter(Long bookerId, LocalDateTime from, LocalDateTime to);

	@Query("SELECT r from Reservation r " +
			"WHERE (r.court.club.id = ?1) " +
			"AND (r.timeFrom >= cast(?2 as localdatetime)) " +
			"AND (?3 IS NULL OR r.isConfirmed = ?3)")
	List<Reservation> findAllByClubAndDateFromWithFilter(Long clubId, LocalDateTime from, Boolean confirmed);

	@Query("SELECT r from Reservation r " +
			"WHERE (r.court.id = ?1) " +
			"AND (cast(?2 as localdatetime) IS NULL OR r.timeFrom >= ?2) " +
			"AND (cast(?3 as localdatetime) IS NULL OR r.timeFrom <= ?3) " +
			"AND r.isCanceled = false ")
	List<Reservation> findAllByCourtWithNotMandatoryDateFilters(Long courtId, LocalDateTime from, LocalDateTime to);

	@Query("SELECT r from Reservation r " +
			"WHERE (r.court.id = ?1) " +
			"AND (r.timeFrom < ?3) " +
			"AND (r.timeTo > ?2) " +
			"AND r.isCanceled = false ")
	List<Reservation> findAllByCourtWithBothDateFilters(Long courtId, LocalDateTime from, LocalDateTime to);
}
