package pl.chopy.reserve_court_backend.infrastructure.user;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import pl.chopy.reserve_court_backend.model.entity.User;
import pl.chopy.reserve_court_backend.model.entity.repository.UserRepository;

@Component
@AllArgsConstructor
public class UserUtil {
	private final UserRepository userRepository;

	public User getCurrentUser() {
		String username = Option.of(SecurityContextHolder.getContext().getAuthentication())
				.map(Authentication::getName)
				.getOrElseThrow(() ->
						new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated.")
				);

		return Option.ofOptional(userRepository.findByEmail(username))
				.getOrElseThrow(() ->
						new ResponseStatusException(HttpStatus.NOT_FOUND, "User '" + username + "' not found.")
				);
	}

	public User getCurrentUserOrNull() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
			return getCurrentUser();
		}
		return null;
	}

	public User getUserById(Long userId) {
		return Option.ofOptional(userRepository.findById(userId))
				.getOrElseThrow(() ->
						new ResponseStatusException(HttpStatus.NOT_FOUND, "User '" + userId + "' not found.")
				);
	}

	public void saveUser(User user) {
		Option.of(userRepository.save(user))
				.getOrElseThrow(
						() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, user.toString())
				);
	}

	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(
						() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
				);
	}
}
