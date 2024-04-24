package pl.chopy.reserve_court_backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Data
public class Reservation {
	@Id
	@GeneratedValue
	private Long id;
	@ManyToOne
	@ToString.Exclude
	@JoinColumn(name = "court_id")
	private Court court;
	@ManyToOne
	@JoinColumn(name = "booker_id")
	private User booker;
	private boolean isConfirmed = false;
	private boolean isCanceled = false;
	private boolean reservedByOwner = false;
	private LocalDateTime timeFrom;
	private LocalDateTime timeTo;
	private String message;

	public boolean areReservationsConcurrent(Reservation other) {
		return !(timeTo.isBefore(other.timeFrom) || timeFrom.isAfter(other.timeTo));
	}

}
