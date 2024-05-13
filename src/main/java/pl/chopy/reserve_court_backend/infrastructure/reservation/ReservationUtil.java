package pl.chopy.reserve_court_backend.infrastructure.reservation;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import pl.chopy.reserve_court_backend.model.entity.Court;
import pl.chopy.reserve_court_backend.model.entity.Reservation;
import pl.chopy.reserve_court_backend.model.entity.User;
import pl.chopy.reserve_court_backend.model.entity.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
public class ReservationUtil {
	private final ReservationRepository reservationRepository;

	public Reservation getById(Long reservationId) {
		return Option.ofOptional(reservationRepository.findById(reservationId))
				.getOrElseThrow(
						() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found")
				);
	}

	public Reservation getActiveById(Long reservationId) {
		return Option.of(getById(reservationId))
				.filter(r -> !r.isCanceled() && r.getTimeFrom().isAfter(LocalDateTime.now()))
				.getOrElseThrow(
						() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found")
				);
	}

	public void save(Reservation reservation) {
		Option.of(reservationRepository.save(reservation))
				.getOrElseThrow(
						() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, reservation.toString())
				);
	}

	public List<Reservation> getAllActiveByCourt(Court court) {
		return reservationRepository.findAllByCourt(court)
				.stream()
				.filter(r -> r.getTimeFrom().isAfter(LocalDateTime.now())
						&& !r.isCanceled()
				)
				.toList();
	}

	public void cancel(Long reservationId, User user) {
		Reservation reservation = getById(reservationId);

		if (!user.getRole().equals(User.UserRole.ADMIN) && !reservation.getBooker().equals(user)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the booker");
		}

		reservation.setCanceled(true);
		//todo send mail to owner and booker
		reservationRepository.save(reservation);
	}
}
