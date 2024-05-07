package pl.chopy.reserve_court_backend.infrastructure.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import pl.chopy.reserve_court_backend.infrastructure.mail.dto.MailSingleRequest;


@Service
@AllArgsConstructor
public class MailService {
	private final JavaMailSender mailSender;
	private final TemplateEngine templateEngine;

	@RabbitListener(queues = "mailingQueue")
	public void sendEmailWithHtmlTemplate(MailSingleRequest request) {
		var message = new Context();
		message.setVariable("email", request.getEmail());

		for (var variable : request.getVariables().keySet()) {
			message.setVariable(variable, request.getVariables().get(variable));
		}

		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

		try {
			helper.setTo(request.getEmail());
			helper.setFrom("the.immortalcoders@gmail.com");
			helper.setSubject(request.getSubject());
			String htmlContent = templateEngine.process(request.getTemplateName(), message);
			helper.setText(htmlContent, true);
			mailSender.send(mimeMessage);
		} catch (MessagingException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email '" + request.getEmail() + "' does not exist.");
		}
	}

}
