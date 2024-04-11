package pl.chopy.reserve_court_backend.infrastructure.court;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.chopy.reserve_court_backend.infrastructure.club.ClubUtil;
import pl.chopy.reserve_court_backend.infrastructure.court.dto.CourtMapper;
import pl.chopy.reserve_court_backend.infrastructure.court.dto.CourtSingleRequest;
import pl.chopy.reserve_court_backend.infrastructure.image.ImageUtil;
import pl.chopy.reserve_court_backend.model.entity.Club;
import pl.chopy.reserve_court_backend.model.entity.repository.CourtRepository;

@Component
@AllArgsConstructor
public class CourtManageUseCase {
    private final CourtUtil courtUtil;
    private final ClubUtil clubUtil;
    private final CourtMapper courtMapper;
    private final CourtRepository courtRepository;
    private final ImageUtil imageUtil;

    public void add(Long clubId, CourtSingleRequest request) {
        Club club = clubUtil.getById(clubId);

        Option.of(request)
                .map(courtMapper::map)
                .peek(c -> {
                    c.setClub(club);
                    c.setImages(imageUtil.getImagesByIds(request.getImagesIds()));
                    courtUtil.save(c);

                    club.getCourts().add(c);
                    clubUtil.save(club);
                });
    }
}
