package pl.chopy.reserve_court_backend.infrastructure.reservation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.chopy.reserve_court_backend.infrastructure.club.ClubUtil;
import pl.chopy.reserve_court_backend.infrastructure.court.CourtUtil;
import pl.chopy.reserve_court_backend.infrastructure.reservation.dto.ReservationMapper;
import pl.chopy.reserve_court_backend.infrastructure.user.UserUtil;
import pl.chopy.reserve_court_backend.model.entity.repository.ReservationRepository;

@Service
@AllArgsConstructor
public class ReservationService {
	private final ReservationUtil reservationUtil;
	private final ReservationMapper reservationMapper;
	private final ReservationRepository reservationRepository;
	private final UserUtil userUtil;
	private final CourtUtil courtUtil;
	private final ClubUtil clubUtil;
}
