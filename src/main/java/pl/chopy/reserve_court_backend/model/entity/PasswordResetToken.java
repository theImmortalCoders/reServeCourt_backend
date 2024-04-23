package pl.chopy.reserve_court_backend.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class PasswordResetToken {
	@Id
	@GeneratedValue
	private Long id;
	private String email;
	private String token;
	private boolean used;
	private LocalDateTime expiring;
}
