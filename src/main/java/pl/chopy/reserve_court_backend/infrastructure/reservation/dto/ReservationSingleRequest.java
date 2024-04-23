package pl.chopy.reserve_court_backend.infrastructure.reservation.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationSingleRequest {
	private Long courtId;
	private Long bookerId;
	private LocalDateTime from;
	private LocalDateTime to;
	private String message;
}
