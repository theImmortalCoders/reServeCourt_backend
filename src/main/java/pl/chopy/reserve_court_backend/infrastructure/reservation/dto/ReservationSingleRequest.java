package pl.chopy.reserve_court_backend.infrastructure.reservation.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationSingleRequest {
	private LocalDateTime timeFrom;
	private LocalDateTime timeTo;
	private String message;
}
