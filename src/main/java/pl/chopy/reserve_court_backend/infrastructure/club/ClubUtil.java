package pl.chopy.reserve_court_backend.infrastructure.club;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import pl.chopy.reserve_court_backend.model.entity.Club;
import pl.chopy.reserve_court_backend.model.entity.repository.ClubRepository;

@Component
@AllArgsConstructor
public class ClubUtil {
    private final ClubRepository clubRepository;

    public Club getById(Long clubId) {
        return clubRepository.findById(clubId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Club not found")
                );
    }

    public void save(Club club) {
        Option.of(clubRepository.save(club))
                .getOrElseThrow(
                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, club.toString())
                );
    }
}
