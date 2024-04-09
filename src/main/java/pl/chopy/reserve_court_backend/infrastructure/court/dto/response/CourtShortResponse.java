package pl.chopy.reserve_court_backend.infrastructure.court.dto.response;

import lombok.Data;
import pl.chopy.reserve_court_backend.infrastructure.image.dto.ImageSingleResponse;
import pl.chopy.reserve_court_backend.model.Location;
import pl.chopy.reserve_court_backend.model.entity.Court;

@Data
public class CourtShortResponse {
    private Long id;
    private String name;
    private Long clubId;
    private Court.CourtType type;
    private Court.Surface surface;
    private Location location;
    private boolean closed;
    private ImageSingleResponse image;
}
