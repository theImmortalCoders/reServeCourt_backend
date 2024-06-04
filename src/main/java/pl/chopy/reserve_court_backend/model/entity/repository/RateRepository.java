package pl.chopy.reserve_court_backend.model.entity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.chopy.reserve_court_backend.model.entity.Rate;

import java.util.List;

public interface RateRepository extends JpaRepository<Rate, Long> {
	List<Rate> findAllByUserIdAndClubId(Long userId, Long clubId);
}
