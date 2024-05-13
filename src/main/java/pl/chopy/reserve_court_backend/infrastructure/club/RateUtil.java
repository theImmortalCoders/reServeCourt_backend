package pl.chopy.reserve_court_backend.infrastructure.club;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import pl.chopy.reserve_court_backend.model.entity.Rate;
import pl.chopy.reserve_court_backend.model.entity.repository.RateRepository;

import java.util.List;

@Component
@AllArgsConstructor
public class RateUtil {
	private final RateRepository rateRepository;

	public void save(Rate request) {
		Option.of(rateRepository.save(request))
				.getOrElseThrow(
						() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, request.toString())
				);
	}

	public void delete(Rate rate) {
		rateRepository.deleteById(rate.getId());
	}

	public List<Rate> findAllByUserAndClub(Long userId, Long clubId) {
		return rateRepository.findAllByUserIdAndClubId(userId, clubId);
	}
}
