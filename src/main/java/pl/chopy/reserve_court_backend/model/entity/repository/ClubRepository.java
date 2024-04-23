package pl.chopy.reserve_court_backend.model.entity.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.chopy.reserve_court_backend.model.entity.Club;

public interface ClubRepository extends JpaRepository<Club, Long> {
	@Query(
			"SELECT c from Club c " +
					"WHERE (?1 IS NULL OR c.owner.name = ?1 OR c.owner.surname = ?1 OR CONCAT(c.owner.name, ' ', c.owner.surname) = ?1) " +
					"AND (?2 IS NULL OR c.name = ?2) " +
					"AND (?3 IS NULL OR c.rating >= ?3) " +
					"AND (?4 IS NULL OR c.rating <= ?3)"
	)
	Page<Club> findAllWithFilters(String ownerName, String name, Double minRating, Double maxRating, PageRequest pageRequest);
}
