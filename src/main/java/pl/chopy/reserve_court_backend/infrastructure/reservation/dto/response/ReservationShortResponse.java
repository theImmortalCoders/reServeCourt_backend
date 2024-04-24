package pl.chopy.reserve_court_backend.infrastructure.reservation.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationShortResponse {
	private Long id;
	private boolean isConfirmed;
	private boolean isCanceled;
	private boolean reservedByOwner;
	private LocalDateTime timeFrom;
	private LocalDateTime timeTo;
}
