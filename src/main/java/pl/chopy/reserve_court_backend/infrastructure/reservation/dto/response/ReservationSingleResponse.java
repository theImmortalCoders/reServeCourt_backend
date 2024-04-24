package pl.chopy.reserve_court_backend.infrastructure.reservation.dto.response;

import lombok.Data;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.response.UserShortResponse;

import java.time.LocalDateTime;

@Data
public class ReservationSingleResponse {
	private Long id;
	private UserShortResponse booker;
	private boolean isConfirmed;
	private boolean isCanceled;
	private boolean reservedByOwner;
	private LocalDateTime timeFrom;
	private LocalDateTime timeTo;
	private String message;
}
