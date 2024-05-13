package pl.chopy.reserve_court_backend.infrastructure.mail;

import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import pl.chopy.reserve_court_backend.infrastructure.mail.dto.MailSingleRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class MailUtil {
	private final RabbitTemplate rabbitTemplate;

	public void sendWelcomeEmail(String email, String name) {
		rabbitTemplate.convertAndSend("mailingQueue", new MailSingleRequest(
				email,
				"Witaj w ReServeCourt, " + name + "!",
				"welcome",
				new HashMap<>(Map.of("name", name)))
		);
	}

	public void sendPasswordResetEmail(String email, String token) {
		rabbitTemplate.convertAndSend("mailingQueue", new MailSingleRequest(
				email,
				"Resetowanie hasła w ReServeCourt",
				"password_reset",
				new HashMap<>(Map.of("email", email, "token", token)))
		);
	}

	public void sendReservationInfoEmail(String email, String name, String clubName, String courtName, LocalDateTime from, LocalDateTime to, String message) {
		rabbitTemplate.convertAndSend("mailingQueue", new MailSingleRequest(
				email,
				"Witaj w ReServeCourt, " + name + "!",
				"reservation",
				new HashMap<>(Map.of("name", name, "clubName", clubName, "courtName", courtName, "from", from, "to", to, "message", message)))
		);
	}
}
