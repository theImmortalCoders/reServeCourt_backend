package pl.chopy.reserve_court_backend.infrastructure.mail;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class MailTemplateService {
	private final MailSendingUtil emailSendingUtil;

	public void sendWelcomeEmail(String email, String name) {
		var message = new Context();
		message.setVariable("email", email);
		message.setVariable("name", name);
		emailSendingUtil.sendEmailWithHtmlTemplate(email, "Witaj w ReServeCourt, " + name + "!", "welcome", message);
	}

	public void sendPasswordResetEmail(String email, String token) {
		var message = new Context();
		message.setVariable("email", email);
		message.setVariable("token", token);
		emailSendingUtil.sendEmailWithHtmlTemplate(email, "Resetowanie hasła w ReServeCourt", "password_reset", message);
	}

	public void sendReservationInfoEmail(String email, String receiverName, String clubName, String courtName, LocalDateTime from, LocalDateTime to, String text) {
		var message = new Context();
		message.setVariable("email", email);
		message.setVariable("receiverName", receiverName);
		message.setVariable("clubName", clubName);
		message.setVariable("courtName", courtName);
		message.setVariable("from", from);
		message.setVariable("to", to);
		message.setVariable("text", text);
		emailSendingUtil.sendEmailWithHtmlTemplate(email, "Informacje o rezerwacji", "reservation", message);
	}
}
