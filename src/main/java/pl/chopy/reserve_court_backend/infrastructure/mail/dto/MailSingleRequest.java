package pl.chopy.reserve_court_backend.infrastructure.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;

@Data
@AllArgsConstructor
public class MailSingleRequest {
	private String email;
	private String subject;
	private String templateName;
	private HashMap<String, Object> variables;
}
