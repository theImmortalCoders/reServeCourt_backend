package pl.chopy.reserve_court_backend.infrastructure.court.dto;

import lombok.Data;
import pl.chopy.reserve_court_backend.model.Location;
import pl.chopy.reserve_court_backend.model.entity.Court;

import java.util.List;

@Data
public class CourtSingleRequest {
    private String name;
    private String description;
    private Court.CourtType type;
    private Court.Surface surface;
    private Location location;
    private List<Long> imagesIds;
}
