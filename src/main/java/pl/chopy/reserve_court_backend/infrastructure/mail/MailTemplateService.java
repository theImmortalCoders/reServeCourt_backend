package pl.chopy.reserve_court_backend.infrastructure.mail;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

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
}
