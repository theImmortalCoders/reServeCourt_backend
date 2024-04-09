package pl.chopy.reserve_court_backend.infrastructure.club.dto.response;

import lombok.Data;
import pl.chopy.reserve_court_backend.infrastructure.image.dto.ImageSingleResponse;
import pl.chopy.reserve_court_backend.model.Location;

@Data
public class ClubShortResponse {
    private Long id;
    private String name;
    private String description;
    private ImageSingleResponse logo;
    private Location location;
    private double rating;
}
