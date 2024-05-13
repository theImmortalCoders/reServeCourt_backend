package pl.chopy.reserve_court_backend.infrastructure.reservation;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.chopy.reserve_court_backend.infrastructure.court.CourtUtil;
import pl.chopy.reserve_court_backend.infrastructure.mail.MailUtil;
import pl.chopy.reserve_court_backend.infrastructure.notification.NotificationUtil;
import pl.chopy.reserve_court_backend.infrastructure.reservation.dto.ReservationMapper;
import pl.chopy.reserve_court_backend.infrastructure.reservation.dto.ReservationSingleRequest;
import pl.chopy.reserve_court_backend.infrastructure.reservation.dto.response.ReservationShortResponse;
import pl.chopy.reserve_court_backend.infrastructure.reservation.dto.response.ReservationSingleResponse;
import pl.chopy.reserve_court_backend.infrastructure.user.UserUtil;
import pl.chopy.reserve_court_backend.model.entity.Club;
import pl.chopy.reserve_court_backend.model.entity.Court;
import pl.chopy.reserve_court_backend.model.entity.Reservation;
import pl.chopy.reserve_court_backend.model.entity.User;
import pl.chopy.reserve_court_backend.model.entity.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ReservationService {
	private final ReservationUtil reservationUtil;
	private final ReservationMapper reservationMapper;
	private final ReservationRepository reservationRepository;
	private final UserUtil userUtil;
	private final CourtUtil courtUtil;
	private final MailUtil mailUtil;
	private final NotificationUtil notificationUtil;

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
				.peek(r -> validate(r, false))
				.peek(r -> {
					reservationUtil.save(r);
					court.getReservations().add(r);
					courtUtil.save(court);
				})
				.peek(r -> mailUtil.sendReservationInfoEmail(
						booker.getEmail(),
						booker.getName(),
						court.getClub().getName(),
						court.getName(),
						r.getTimeFrom(),
						r.getTimeTo(),
						"Potwierdzenie rezerwacji: zarezerwowano poprawnie!"
				))
				.peek(r -> notificationUtil.sendManagementNotification(booker.getId(), "Zarezerwowano"))
				.map(reservationMapper::map)
				.get();
	}

	public ReservationSingleResponse update(ReservationSingleRequest request, Long reservationId) {
		User booker = userUtil.getCurrentUser();
		Reservation reservation = reservationUtil.getById(reservationId);

		if (!booker.getId().equals(reservation.getBooker().getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the booker");
		}

		return Option.of(reservation)
				.peek(r -> {
					r.setTimeFrom(request.getTimeFrom());
					r.setTimeTo(request.getTimeTo());
					r.setMessage(request.getMessage());
					r.setConfirmed(false);
				})
				.peek(r -> validate(r, true))
				.peek(reservationUtil::save)
				.peek(r -> mailUtil.sendReservationInfoEmail(
						booker.getEmail(),
						booker.getName(),
						r.getCourt().getClub().getName(),
						r.getCourt().getName(),
						r.getTimeFrom(),
						r.getTimeTo(),
						"Potwierdzenie rezerwacji: rezerwacja została poprawnie zaktualizowana!"
				))
				.map(reservationMapper::map)
				.get();
	}

	public void cancel(Long reservationId) {
		User booker = userUtil.getCurrentUser();
		Reservation reservation = reservationUtil.getActiveById(reservationId);

		checkIfAdminOrBooker(booker, reservation);

		reservation.setCanceled(true);
		reservationUtil.save(reservation);

		mailUtil.sendReservationInfoEmail(
				booker.getEmail(),
				booker.getName(),
				reservation.getCourt().getClub().getName(),
				reservation.getCourt().getName(),
				reservation.getTimeFrom(),
				reservation.getTimeTo(),
				"Rezerwacja została anulowana."
		);
	}

	public void confirm(Long reservationId) {
		Reservation reservation = reservationUtil.getActiveById(reservationId);
		User booker = reservation.getBooker();

		reservation.setConfirmed(true);
		reservationUtil.save(reservation);

		mailUtil.sendReservationInfoEmail(
				booker.getEmail(),
				booker.getName(),
				reservation.getCourt().getClub().getName(),
				reservation.getCourt().getName(),
				reservation.getTimeFrom(),
				reservation.getTimeTo(),
				"Twoja rezerwacja została potwierdzona przez administratora kortu!"
		);
	}

	public List<ReservationShortResponse> getByCourtWithFilters(Long courtId, LocalDateTime from, LocalDateTime to) {
		return reservationRepository.findAllByCourtWithFilters(courtId, from, to)
				.stream()
				.map(reservationMapper::shortMap)
				.toList();
	}

	public ReservationSingleResponse getDetails(Long reservationId) {
		User booker = userUtil.getCurrentUser();
		Reservation reservation = reservationUtil.getActiveById(reservationId);

		checkIfAdminOrBooker(booker, reservation);

		return reservationMapper.map(reservation);
	}

	public List<ReservationShortResponse> getUpcomingByClubAndConfirmed(Long clubId, Boolean confirmed) {
		return reservationRepository.findAllByClubAndDateFromWithFilter(clubId, LocalDateTime.now(), confirmed)
				.stream()
				.map(reservationMapper::shortMap)
				.toList();
	}

	public List<ReservationShortResponse> getByCurrentUser(LocalDateTime from, LocalDateTime to) {
		User user = userUtil.getCurrentUser();

		return reservationRepository.findAllByBookerWithFilter(user.getId(), from, to)
				.stream()
				.map(reservationMapper::shortMap)
				.toList();
	}

	//

	private static void checkIfAdminOrBooker(User booker, Reservation reservation) {
		if (!booker.getId().equals(reservation.getBooker().getId()) && booker.getRole().equals(User.UserRole.ADMIN)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the booker or admin");
		}
	}

	private void validate(Reservation reservation, boolean updateReservation) {
		Court court = reservation.getCourt();
		Club club = court.getClub();

		if (court.isClosed()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Court is closed");
		}
		if (reservation.getTimeFrom().minusDays(7).isBefore(LocalDateTime.now())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot book later than 7 days before");
		}
		if (reservation.getTimeTo().minusHours(1).isBefore(reservation.getTimeFrom())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reservation duration minimum is 1h");
		}
		if (!club.getDaysOpen().checkIsDateIntervalInOpeningHours(reservation.getTimeFrom().plusMinutes(1), reservation.getTimeTo().minusMinutes(1))) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reservation time not matching club opening hours");
		}
		if (updateReservation) {
			if (!isCourtFreeInHoursDespiteCurrent(reservation, court)) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Court is already occupied in desired time.");
			}
		} else {
			if (!isCourtFreeInHours(reservation, court)) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Court is already occupied in desired time.");
			}
		}
	}

	private boolean isCourtFreeInHours(Reservation reservation, Court court) {
		for (var r : reservationUtil.getAllActiveByCourt(court)) {
			if (r.areReservationsConcurrent(reservation)) return false;
		}
		return true;
	}

	private boolean isCourtFreeInHoursDespiteCurrent(Reservation reservation, Court court) {
		List<Reservation> reservations = new ArrayList<>(reservationUtil.getAllActiveByCourt(court));
		reservations.removeIf(r -> r.getId().equals(reservation.getId()));
		for (var r : reservations) {
			if (r.areReservationsConcurrent(reservation)) return false;
		}
		return true;
	}
}
