package pl.chopy.reserve_court_backend.infrastructure.user;

import io.vavr.control.Option;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.chopy.reserve_court_backend.infrastructure.mail.MailUtil;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.UserMapper;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.request.UserChangePasswordRequest;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.request.UserSingleLoginRequest;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.request.UserSingleRegisterRequest;
import pl.chopy.reserve_court_backend.model.entity.PasswordResetToken;
import pl.chopy.reserve_court_backend.model.entity.User;
import pl.chopy.reserve_court_backend.model.entity.repository.PasswordResetTokenRepository;
import pl.chopy.reserve_court_backend.model.entity.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {
	private final UserRepository userRepository;
	private final PasswordResetTokenRepository passwordResetTokenRepository;
	private final UserUtil userUtil;
	private final BCryptPasswordEncoder passwordEncoder;
	private final MailUtil mailUtil;
	private final UserMapper userMapper;
	private final SecurityContextHolderStrategy securityContextHolderStrategy;
	private final SecurityContextRepository securityContextRepository;

	void authenticate(@NotNull UserSingleLoginRequest userRequest, HttpServletRequest request, HttpServletResponse response) {
		User user = Option.ofOptional(getOptionalByEmail(userRequest.getEmail()))
				.filter(u -> passwordEncoder.matches(userRequest.getPassword(), u.getHashedPassword()))
				.getOrElseThrow(() ->
						new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials for user '" + userRequest.getEmail() + "'.")
				);

		updateSecurityContext(request, response, user);
	}

	void register(@NotNull UserSingleRegisterRequest request) {
		Option.ofOptional(getOptionalByEmail(request.getEmail()))
				.map(user -> {
					throw new ResponseStatusException(HttpStatus.CONFLICT, "User '" + request.getEmail() + "' already exists");
				});

		if (Period.between(request.getBirthDate(), LocalDate.now()).getYears() < 15) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not have 15 years old");
		}

		User user = userMapper.map(request);
		user.setHashedPassword(passwordEncoder.encode(request.getPassword()));

		userUtil.saveUser(user);
		mailUtil.sendWelcomeEmail(user.getEmail(), user.getName());
	}

	void changePassword(@NotNull UserChangePasswordRequest request) {
		User user = userUtil.getCurrentUser();

		if (!passwordEncoder.matches(request.getOldPassword(), user.getHashedPassword())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid old password");
		}

		user.setHashedPassword(passwordEncoder.encode(request.getNewPassword()));
		userUtil.saveUser(user);
	}

	void requestPasswordReset(String email) {
		User user = userUtil.getUserByEmail(email);

		PasswordResetToken token = new PasswordResetToken();
		token.setToken(UUID.randomUUID().toString());
		token.setEmail(user.getEmail());
		token.setExpiring(LocalDateTime.now().plusMinutes(10));
		passwordResetTokenRepository.save(token);

		mailUtil.sendPasswordResetEmail(user.getEmail(), token.getToken());
	}

	public void resetPassword(String tokenValue, String newPassword) {
		PasswordResetToken token = passwordResetTokenRepository.findByToken(tokenValue)
				.filter(t -> t.getExpiring().isAfter(LocalDateTime.now()))
				.orElseThrow(
						() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token invalid")
				);

		User user = userUtil.getUserByEmail(token.getEmail());

		user.setHashedPassword(passwordEncoder.encode(newPassword));
		userUtil.saveUser(user);
	}

	void updateSecurityContext(HttpServletRequest request, HttpServletResponse response, User user) {
		SecurityContext context = securityContextHolderStrategy.createEmptyContext();
		context.setAuthentication(new UsernamePasswordAuthenticationToken(
				user.getEmail(),
				user.getHashedPassword(),
				user.getAuthorities()
		));

		securityContextHolderStrategy.setContext(context);
		securityContextRepository.saveContext(context, request, response);
	}

	void logoutUser(HttpServletRequest request, HttpServletResponse response) {
		SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
		logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());
		SecurityContextHolder.getContext().setAuthentication(null);
	}

	//

	private Optional<User> getOptionalByEmail(String email) {
		return userRepository.findByEmail(email);
	}
}
