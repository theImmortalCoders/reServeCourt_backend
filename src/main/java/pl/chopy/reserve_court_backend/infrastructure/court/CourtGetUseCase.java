package pl.chopy.reserve_court_backend.infrastructure.court;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.chopy.reserve_court_backend.infrastructure.court.dto.CourtMapper;
import pl.chopy.reserve_court_backend.infrastructure.court.dto.response.CourtShortResponse;
import pl.chopy.reserve_court_backend.model.entity.Court;
import pl.chopy.reserve_court_backend.model.entity.repository.CourtRepository;
import pl.chopy.reserve_court_backend.model.entity.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
public class CourtGetUseCase {
	private final CourtRepository courtRepository;
	private final CourtMapper courtMapper;
	private final ReservationRepository reservationRepository;

	public List<CourtShortResponse> getAvailable(
			LocalDateTime from,
			LocalDateTime to,
			Court.CourtType courtType,
			Court.Surface surface,
			String locationName
	) {
		List<Court> courts = courtRepository.findAllWithFilters(surface, courtType, locationName);

		return courts.stream()
				.filter(c -> c.getClub().getDaysOpen().checkIsDateIntervalInOpeningHours(from, to))
				.filter(
						c -> reservationRepository.findAllByCourtWithBothDateFilters(c.getId(), from, to)
								.isEmpty()
				)
				.map(courtMapper::shortMap)
				.toList();
	}
}
