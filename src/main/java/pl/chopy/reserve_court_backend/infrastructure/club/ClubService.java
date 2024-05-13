package pl.chopy.reserve_court_backend.infrastructure.club;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.chopy.reserve_court_backend.infrastructure.club.dto.ClubMapper;
import pl.chopy.reserve_court_backend.infrastructure.club.dto.ClubSingleRequest;
import pl.chopy.reserve_court_backend.infrastructure.club.dto.response.ClubShortResponse;
import pl.chopy.reserve_court_backend.infrastructure.club.dto.response.ClubSingleResponse;
import pl.chopy.reserve_court_backend.infrastructure.court.dto.response.CourtShortResponse;
import pl.chopy.reserve_court_backend.infrastructure.image.ImageUtil;
import pl.chopy.reserve_court_backend.infrastructure.notification.NotificationUtil;
import pl.chopy.reserve_court_backend.infrastructure.reservation.ReservationUtil;
import pl.chopy.reserve_court_backend.infrastructure.user.UserUtil;
import pl.chopy.reserve_court_backend.model.DaysOpen;
import pl.chopy.reserve_court_backend.model.entity.Club;
import pl.chopy.reserve_court_backend.model.entity.Rate;
import pl.chopy.reserve_court_backend.model.entity.User;
import pl.chopy.reserve_court_backend.model.entity.repository.ClubRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ClubService {
	private final ClubUtil clubUtil;
	private final ClubMapper clubMapper;
	private final ImageUtil imageUtil;
	private final ClubRepository clubRepository;
	private final UserUtil userUtil;
	private final RateUtil rateUtil;
	private final ReservationUtil reservationUtil;
	private final NotificationUtil notificationUtil;

	void add(ClubSingleRequest request) {
		Option.of(request)
				.map(clubMapper::map)
				.peek(c -> {
					updateLogo(request, c);
					c.setOwner(userUtil.getCurrentUser());
					if (c.getDaysOpen() == null) {
						c.setDaysOpen(new DaysOpen());
					}
					checkOpenDaysValid(c.getDaysOpen());
				})
				.peek(clubUtil::save);

		notificationUtil.sendManagementNotification(userUtil.getCurrentUser().getId(), "Dodano klub");
	}

	void update(Long clubId, ClubSingleRequest request) {
		Club club = clubUtil.getById(clubId);

		Option.of(club)
				.peek(c -> {
					updateLogo(request, c);
					c.setName(request.getName());
					c.setDescription(request.getDescription());
					c.setLocation(request.getLocation());
					c.setDaysOpen(request.getDaysOpen());
					if (c.getDaysOpen() == null) {
						c.setDaysOpen(new DaysOpen());
					}
					checkOpenDaysValid(c.getDaysOpen());
				})
				.peek(clubUtil::save);
	}

	public void delete(Long clubId) {
		Club club = clubUtil.getById(clubId);

		if (!club.getCourts()
				.stream()
				.filter(c -> !reservationUtil.getAllActiveByCourt(c).isEmpty())
				.toList()
				.isEmpty()
		) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Club has active reservations");
		}

		clubRepository.delete(club);
	}

	public Page<ClubShortResponse> getAll(PageRequest pageRequest, String ownerName, String name) {
		return new PageImpl<>(
				clubRepository.findAllWithFilters(
								ownerName,
								name,
								pageRequest
						).stream()
						.map(clubMapper::shortMap)
						.toList(),
				pageRequest,
				clubRepository.count()
		);
	}

	public ClubSingleResponse getDetails(Long clubId) {
		return Option.of(clubUtil.getById(clubId))
				.map(clubMapper::map)
				.peek(c -> {
					User user = userUtil.getCurrentUserOrNull();

					if (user == null || user.getRole().equals(User.UserRole.USER)) {
						c.setCourts(filterCourtsByNotClosed(c));
					}


				})
				.get();
	}

	public void rate(Long clubId, double rating) {
		if (rating < 0 || rating > 5) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating must be between 0 and 5");
		}

		Club club = clubUtil.getById(clubId);
		User user = userUtil.getCurrentUser();

		List<Rate> existingRatings = rateUtil.findAllByUserAndClub(user.getId(), clubId);
		for (var r : existingRatings) {
			rateUtil.delete(r);
		}

		Rate rate = new Rate();
		rate.setClub(club);
		rate.setUser(user);
		rate.setValue(rating);

		rateUtil.save(rate);

		club.getRates().add(rate);
		clubUtil.save(club);
	}

	//

	private static void checkOpenDaysValid(DaysOpen daysOpen) {
		if (!daysOpen.checkIsValid()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Open days is invalid");
		}
	}

	private static ArrayList<CourtShortResponse> filterCourtsByNotClosed(ClubSingleResponse c) {
		return new ArrayList<>(c.getCourts()
				.stream()
				.filter(co -> !co.isClosed())
				.toList());
	}

	private void updateLogo(ClubSingleRequest request, Club c) {
		if (request.getLogoId() != null) {
			c.setLogo(imageUtil.getImageById(request.getLogoId()));
		}
	}
}
