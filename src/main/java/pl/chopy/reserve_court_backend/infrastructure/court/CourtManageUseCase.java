package pl.chopy.reserve_court_backend.infrastructure.court;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import pl.chopy.reserve_court_backend.infrastructure.club.ClubUtil;
import pl.chopy.reserve_court_backend.infrastructure.court.dto.CourtMapper;
import pl.chopy.reserve_court_backend.infrastructure.court.dto.CourtSingleRequest;
import pl.chopy.reserve_court_backend.infrastructure.court.dto.response.CourtSingleResponse;
import pl.chopy.reserve_court_backend.infrastructure.image.ImageUtil;
import pl.chopy.reserve_court_backend.infrastructure.notification.NotificationUtil;
import pl.chopy.reserve_court_backend.infrastructure.user.UserUtil;
import pl.chopy.reserve_court_backend.model.entity.Club;
import pl.chopy.reserve_court_backend.model.entity.Court;
import pl.chopy.reserve_court_backend.model.entity.Reservation;
import pl.chopy.reserve_court_backend.model.entity.repository.CourtRepository;

@Component
@AllArgsConstructor
public class CourtManageUseCase {
	private final CourtUtil courtUtil;
	private final ClubUtil clubUtil;
	private final CourtMapper courtMapper;
	private final CourtRepository courtRepository;
	private final ImageUtil imageUtil;
	private final NotificationUtil notificationUtil;
	private final UserUtil userUtil;

	public void add(Long clubId, CourtSingleRequest request) {
		Club club = clubUtil.getById(clubId);

		Option.of(request)
				.map(courtMapper::map)
				.peek(court -> {
					court.setClub(club);
					court.setImages(imageUtil.getImagesByIds(request.getImagesIds()));
					courtUtil.save(court);

					club.getCourts().add(court);
					clubUtil.save(club);
				});

		notificationUtil.sendManagementNotification(
				userUtil.getCurrentUser().getId(),
				"Dodano kort " + request.getName() + " do klubu " + club.getName() + "."
		);
	}

	public void update(Long courtId, CourtSingleRequest request) {
		Court court = courtUtil.getById(courtId);

		courtMapper.update(court, request);
		court.setImages(imageUtil.getImagesByIds(request.getImagesIds()));

		courtUtil.save(court);
	}

	public void delete(Long courtId) {
		Court court = courtUtil.getById(courtId);

		if (hasActiveReservations(court)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Court has active reservations");
		}

		courtRepository.delete(court);
	}

	public CourtSingleResponse getDetails(Long courtId) {
		return courtMapper.map(
				courtUtil.getById(courtId)
		);
	}

	//

	private boolean hasActiveReservations(Court court) {
		return !Option.of(court)
				.filter(c -> !c.getReservations().stream()
						.filter(Reservation::isActive)
						.toList()
						.isEmpty())
				.toList()
				.isEmpty();
	}
}
