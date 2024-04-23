package pl.chopy.reserve_court_backend.infrastructure.club.dto.response;

import lombok.Data;
import pl.chopy.reserve_court_backend.infrastructure.court.dto.response.CourtShortResponse;
import pl.chopy.reserve_court_backend.infrastructure.image.dto.ImageSingleResponse;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.response.UserShortResponse;
import pl.chopy.reserve_court_backend.model.DaysOpen;
import pl.chopy.reserve_court_backend.model.Location;

import java.util.List;

@Data
public class ClubSingleResponse {
	private Long id;
	private String name;
	private String description;
	private ImageSingleResponse logo;
	private Location location;
	private List<CourtShortResponse> courts;
	private UserShortResponse owner;
	private double rating;
	private DaysOpen daysOpen;
}
