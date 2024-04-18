package pl.chopy.reserve_court_backend.infrastructure.mail;

import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import pl.chopy.reserve_court_backend.infrastructure.mail.dto.MailSingleRequest;

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
		var message = new Context();
		message.setVariable("email", email);
		message.setVariable("token", token);
		rabbitTemplate.convertAndSend("mailingQueue", new MailSingleRequest(
				email,
				"Resetowanie hasła w ReServeCourt",
				"password_reset",
				new HashMap<>())
		);
	}
}
