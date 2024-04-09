package pl.chopy.reserve_court_backend.infrastructure.club.dto;

import lombok.Data;
import pl.chopy.reserve_court_backend.model.Location;

@Data
public class ClubSingleRequest {
    private String name;
    private String description;
    private Location location;
    private Long logoId;
}
