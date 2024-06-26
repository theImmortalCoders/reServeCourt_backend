package pl.chopy.reserve_court_backend.infrastructure.club;

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
import pl.chopy.reserve_court_backend.infrastructure.club.dto.ClubMapper;
import pl.chopy.reserve_court_backend.infrastructure.club.dto.ClubMapperImpl;
import pl.chopy.reserve_court_backend.infrastructure.club.dto.ClubSingleRequest;
import pl.chopy.reserve_court_backend.infrastructure.club.dto.response.ClubSingleResponse;
import pl.chopy.reserve_court_backend.infrastructure.image.ImageUtil;
import pl.chopy.reserve_court_backend.infrastructure.notification.NotificationUtil;
import pl.chopy.reserve_court_backend.infrastructure.reservation.ReservationUtil;
import pl.chopy.reserve_court_backend.infrastructure.user.UserUtil;
import pl.chopy.reserve_court_backend.model.DaysOpen;
import pl.chopy.reserve_court_backend.model.entity.*;
import pl.chopy.reserve_court_backend.model.entity.repository.ClubRepository;
import pl.chopy.reserve_court_backend.model.entity.repository.RateRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ClubServiceTest {
	private Club club;
	private Image image;
	private User user;
	private final ClubRepository clubRepository = mock(ClubRepository.class);
	private final RateRepository rateRepository = mock(RateRepository.class);
	@Spy
	private ClubUtil clubUtil = new ClubUtil(clubRepository);
	@Spy
	private RateUtil rateUtil = new RateUtil(rateRepository);
	@Mock
	private ImageUtil imageUtil;
	@Mock
	private ReservationUtil reservationUtil;
	@Mock
	private NotificationUtil notificationUtil;
	@Spy
	private final ClubMapper clubMapper = new ClubMapperImpl();
	@Mock
	private UserUtil userUtil;
	@InjectMocks
	private ClubService clubService;

	@BeforeEach
	public void setUp() {
		club = new Club();
		club.setId(1L);

		image = new Image();
		image.setId(1L);

		user = new User();
		user.setId(1L);

		when(imageUtil.getImageById(1L)).thenReturn(image);
		when(userUtil.getCurrentUserOrNull()).thenReturn(user);
		when(userUtil.getCurrentUser()).thenReturn(user);
		when(clubRepository.findById(1L)).thenReturn(Optional.of(club));
		when(clubRepository.save(any(Club.class))).thenReturn(club);
	}

	@Test
	public void shouldAddClub() {
		var request = new ClubSingleRequest();
		request.setLogoId(1L);
		request.setDaysOpen(new DaysOpen());

		clubService.add(request);

		verify(clubRepository, times(1)).save(any(Club.class));
	}

	@Test
	public void shouldUpdateClub() {
		var request = new ClubSingleRequest();
		request.setLogoId(1L);
		request.setDaysOpen(new DaysOpen());

		clubService.update(1L, request);

		verify(clubRepository, times(1)).findById(1L);
		verify(clubRepository, times(1)).save(any(Club.class));
	}

	@Test
	public void shouldDeleteClub() {
		clubService.delete(1L);

		verify(clubRepository, times(1)).delete(any(Club.class));
	}

	@Test
	public void shouldThrowBadRequestWhenDeleteClub() {
		var court = new Court();
		var reservation = new Reservation();

		court.setReservations(new ArrayList<>(List.of(reservation)));
		club.getCourts().add(court);

		when(reservationUtil.getAllActiveByCourt(court)).thenReturn(new ArrayList<>(List.of(reservation)));

		assertThrows(
				ResponseStatusException.class,
				() -> clubService.delete(1L)
		);
	}

	@Test
	public void shouldGetDetails() {
		var response = new ClubSingleResponse();
		response.setId(1L);
		response.setCourts(new ArrayList<>());
		response.setDaysOpen(new DaysOpen());

		assertEquals(response, clubService.getDetails(1L));
	}

	@Test
	public void shouldRateClub() {
		var rate = new Rate();
		rate.setId(1L);
		rate.setClub(club);
		rate.setUser(user);

		when(rateRepository.save(any(Rate.class))).thenReturn(rate);

		clubService.rate(1L, 4.0);

		assertEquals(1, club.getRates().size());
	}
}
