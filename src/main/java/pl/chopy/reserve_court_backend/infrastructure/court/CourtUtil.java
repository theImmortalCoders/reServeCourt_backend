package pl.chopy.reserve_court_backend.infrastructure.court;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import pl.chopy.reserve_court_backend.model.entity.Court;
import pl.chopy.reserve_court_backend.model.entity.repository.CourtRepository;

@Component
@AllArgsConstructor
public class CourtUtil {
    private final CourtRepository courtRepository;

    public Court getById(Long courtId) {
        return courtRepository.findById(courtId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Court not found")
                );
    }

    public void save(Court court) {
        Option.of(courtRepository.save(court))
                .getOrElseThrow(
                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, court.toString())
                );
    }
}
