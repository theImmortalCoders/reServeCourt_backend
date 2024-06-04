package pl.chopy.reserve_court_backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
public class Rate {
	@Id
	@GeneratedValue
	private Long id;
	@ManyToOne
	@ToString.Exclude
	@JoinColumn(name = "club_id")
	private Club club;
	@ManyToOne
	@JoinColumn(name = "user_id")
	@ToString.Exclude
	private User user;
	private double value = 0.0;
}
