package pl.chopy.reserve_court_backend.model.entity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.chopy.reserve_court_backend.model.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String username);
}
