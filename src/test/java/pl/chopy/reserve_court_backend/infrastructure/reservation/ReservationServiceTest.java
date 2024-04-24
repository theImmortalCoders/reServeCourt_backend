package pl.chopy.reserve_court_backend.infrastructure.reservation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.server.ResponseStatusException;
import pl.chopy.reserve_court_backend.infrastructure.court.CourtUtil;
import pl.chopy.reserve_court_backend.infrastructure.mail.MailTemplateService;
import pl.chopy.reserve_court_backend.infrastructure.reservation.dto.ReservationMapper;
import pl.chopy.reserve_court_backend.infrastructure.reservation.dto.ReservationMapperImpl;
import pl.chopy.reserve_court_backend.infrastructure.reservation.dto.ReservationSingleRequest;
import pl.chopy.reserve_court_backend.infrastructure.user.UserUtil;
import pl.chopy.reserve_court_backend.model.entity.Club;
import pl.chopy.reserve_court_backend.model.entity.Court;
import pl.chopy.reserve_court_backend.model.entity.Reservation;
import pl.chopy.reserve_court_backend.model.entity.User;
import pl.chopy.reserve_court_backend.model.entity.repository.ReservationRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ReservationServiceTest {
	private final Reservation reservation = new Reservation();
	private final Court court = new Court();
	private final Club club = new Club();
	private final User user = new User();

	@Mock
	private UserUtil userUtil;
	@Mock
	private CourtUtil courtUtil;
	@Mock
	private MailTemplateService mailTemplateService;
	private final ReservationRepository reservationRepository = mock(ReservationRepository.class);
	@Spy
	private final ReservationMapper reservationMapper = new ReservationMapperImpl();
	@Spy
	private final ReservationUtil reservationUtil = new ReservationUtil(reservationRepository);
	@InjectMocks
	private ReservationService reservationService;

	@BeforeEach
	public void setUp() {
		reservation.setId(1L);
		reservation.setTimeFrom(LocalDateTime.of(LocalDateTime.now().plusYears(1).getYear(), 4, 23, 10, 0));
		reservation.setTimeTo(reservation.getTimeFrom().plusHours(1));
		reservation.setBooker(user);
		reservation.setCourt(court);

		user.setId(1L);
		user.setRole(User.UserRole.USER);

		club.setId(1L);
		club.getCourts().add(court);

		court.setId(1L);
		court.setClub(club);

		when(userUtil.getCurrentUser()).thenReturn(user);
		when(courtUtil.getById(1L)).thenReturn(court);
		when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
		when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
	}

	@Test
	public void shouldAddReservation() {
		var request = new ReservationSingleRequest();
		request.setTimeFrom(reservation.getTimeFrom());
		request.setTimeTo(reservation.getTimeTo());

		reservationService.reserve(request, 1L);

		verify(reservationRepository, times(1)).save(any(Reservation.class));
	}

	@Test
	public void shouldThrowCannotBookLater() {
		var request = new ReservationSingleRequest();
		request.setTimeFrom(LocalDateTime.now());
		request.setTimeTo(LocalDateTime.now().plusHours(1));

		assertThrows(
				ResponseStatusException.class,
				() -> reservationService.reserve(request, 1L)
		);
	}

	@Test
	public void shouldThrowReservationMinimum() {
		var request = new ReservationSingleRequest();
		request.setTimeFrom(reservation.getTimeFrom());
		request.setTimeTo(reservation.getTimeTo().minusMinutes(1));

		assertThrows(
				ResponseStatusException.class,
				() -> reservationService.reserve(request, 1L)
		);
	}

	@Test
	public void shouldThrowOpeningHours() {
		var request = new ReservationSingleRequest();
		request.setTimeFrom(LocalDateTime.of(
				LocalDate.now(),
				LocalTime.of(0, 0)
		));
		request.setTimeTo(request.getTimeFrom().plusHours(2));

		assertThrows(
				ResponseStatusException.class,
				() -> reservationService.reserve(request, 1L)
		);
	}

	@Test
	public void shouldThrowOccupied() {
		var request = new ReservationSingleRequest();
		request.setTimeFrom(reservation.getTimeFrom());
		request.setTimeTo(reservation.getTimeTo().plusHours(2));

		var r = new Reservation();
		r.setId(1L);
		r.setTimeFrom(reservation.getTimeFrom().minusHours(1));
		r.setTimeTo(reservation.getTimeTo().minusHours(1));

		court.getReservations().add(r);
		when(reservationUtil.getAllActiveByCourt(court)).thenReturn(new ArrayList<>(List.of(r)));

		assertThrows(
				ResponseStatusException.class,
				() -> reservationService.reserve(request, 1L)
		);
	}

	@Test
	public void shouldUpdate() {
		var request = new ReservationSingleRequest();
		request.setTimeFrom(reservation.getTimeFrom());
		request.setTimeTo(reservation.getTimeTo());

		reservationService.update(request, 1L);

		verify(reservationRepository, times(1)).save(any(Reservation.class));
	}

	@Test
	public void shouldCancel() {
		reservationService.cancel(1L);

		verify(reservationRepository, times(1)).save(any(Reservation.class));
	}

	@Test
	public void shouldConfirm() {
		reservationService.confirm(1L);

		verify(reservationRepository, times(1)).save(any(Reservation.class));
	}
}
