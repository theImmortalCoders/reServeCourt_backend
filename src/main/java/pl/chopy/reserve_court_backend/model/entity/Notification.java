package pl.chopy.reserve_court_backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Notification {
	@Id
	@GeneratedValue
	private Long id;
	private String message;
	@ManyToOne
	@JoinColumn(name = "receiver_id")
	private User receiver;
}
