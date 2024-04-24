package pl.chopy.reserve_court_backend.infrastructure.reservation;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.chopy.reserve_court_backend.infrastructure.club.ClubUtil;
import pl.chopy.reserve_court_backend.infrastructure.court.CourtUtil;
import pl.chopy.reserve_court_backend.infrastructure.mail.MailTemplateService;
import pl.chopy.reserve_court_backend.infrastructure.reservation.dto.ReservationMapper;
import pl.chopy.reserve_court_backend.infrastructure.reservation.dto.ReservationSingleRequest;
import pl.chopy.reserve_court_backend.infrastructure.reservation.dto.response.ReservationSingleResponse;
import pl.chopy.reserve_court_backend.infrastructure.user.UserUtil;
import pl.chopy.reserve_court_backend.model.entity.Club;
import pl.chopy.reserve_court_backend.model.entity.Court;
import pl.chopy.reserve_court_backend.model.entity.Reservation;
import pl.chopy.reserve_court_backend.model.entity.User;
import pl.chopy.reserve_court_backend.model.entity.repository.ReservationRepository;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ReservationService {
	private final ReservationUtil reservationUtil;
	private final ReservationMapper reservationMapper;
	private final ReservationRepository reservationRepository;
	private final UserUtil userUtil;
	private final CourtUtil courtUtil;
	private final ClubUtil clubUtil;
	private final MailTemplateService mailTemplateService;

	public ReservationSingleResponse reserve(ReservationSingleRequest request, Long courtId) {
		User booker = userUtil.getCurrentUser();
		Court court = courtUtil.getById(courtId);

		return Option.of(request)
				.map(reservationMapper::map)
				.peek(r -> {
					r.setBooker(booker);
					r.setCourt(court);
					if (booker.getRole().equals(User.UserRole.ADMIN)) {
						r.setReservedByOwner(true);
						r.setConfirmed(true);
					}
				})
				.peek(this::validate)
				.peek(r -> {
					reservationUtil.save(r);
					court.getReservations().add(r);
					courtUtil.save(court);
				})
				.map(reservationMapper::map)
				.get();
	}

	//

	private void validate(Reservation reservation) {
		Court court = reservation.getCourt();
		Club club = court.getClub();

		if (reservation.getTimeFrom().minusDays(7).isBefore(LocalDateTime.now())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot book later than 7 days before");
		}
		if (reservation.getTimeTo().minusHours(1).isBefore(reservation.getTimeFrom())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reservation duration minimum is 1h");
		}
		if (!club.getDaysOpen().checkIsDateIntervalInOpeningHours(reservation.getTimeFrom().plusMinutes(1), reservation.getTimeTo().minusMinutes(1))) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reservation time not matching club opening hours");
		}
		if (!isCourtFreeInHours(reservation, court)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Court is already occupied in desired time.");
		}
	}

	private boolean isCourtFreeInHours(Reservation reservation, Court court) {
		for (var r : reservationUtil.getAllActiveByCourt(court)) {
			if (r.areReservationsConcurrent(reservation)) return false;
		}
		return true;
	}
}
