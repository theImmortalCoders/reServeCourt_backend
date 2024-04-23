package pl.chopy.reserve_court_backend.infrastructure.reservation;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import pl.chopy.reserve_court_backend.model.entity.Reservation;
import pl.chopy.reserve_court_backend.model.entity.repository.ReservationRepository;

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

	public void save(Reservation reservation) {
		Option.of(reservationRepository.save(reservation))
				.getOrElseThrow(
						() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, reservation.toString())
				);
	}
}
