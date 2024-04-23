package pl.chopy.reserve_court_backend.infrastructure.reservation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import pl.chopy.reserve_court_backend.infrastructure.club.ClubUtil;
import pl.chopy.reserve_court_backend.infrastructure.court.CourtUtil;
import pl.chopy.reserve_court_backend.infrastructure.reservation.dto.ReservationMapper;
import pl.chopy.reserve_court_backend.infrastructure.reservation.dto.ReservationMapperImpl;
import pl.chopy.reserve_court_backend.infrastructure.user.UserUtil;
import pl.chopy.reserve_court_backend.model.entity.Club;
import pl.chopy.reserve_court_backend.model.entity.Court;
import pl.chopy.reserve_court_backend.model.entity.Reservation;
import pl.chopy.reserve_court_backend.model.entity.User;
import pl.chopy.reserve_court_backend.model.entity.repository.ReservationRepository;

import java.time.LocalDateTime;

import static org.mockito.Mockito.mock;

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
	private ClubUtil clubUtil;
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
		reservation.setFrom(LocalDateTime.of(2024, 4, 23, 10, 0));
		reservation.setTo(LocalDateTime.of(2024, 4, 23, 11, 0));

		user.setId(1L);

		club.setId(1L);

		court.setId(1L);
	}
}
