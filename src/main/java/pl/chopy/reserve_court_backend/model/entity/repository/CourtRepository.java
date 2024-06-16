package pl.chopy.reserve_court_backend.model.entity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.chopy.reserve_court_backend.model.entity.Court;

import java.util.List;

public interface CourtRepository extends JpaRepository<Court, Long> {
	@Query("SELECT c from Court c " +
			"WHERE(?1 IS NULL OR c.surface = ?1) " +
			"AND (?2 IS NULL OR c.type = ?2) " +
			"AND (?3 IS NULL OR lower(cast(c.location as string)) LIKE CONCAT('%', lower(cast(?3 as string)), '%'))")
	List<Court> findAllWithFilters(Court.Surface surface, Court.CourtType type, String location);

	int countAllByType(Court.CourtType type);
}
