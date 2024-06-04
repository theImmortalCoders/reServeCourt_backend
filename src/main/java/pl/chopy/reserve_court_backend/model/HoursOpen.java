package pl.chopy.reserve_court_backend.model;

import lombok.Data;

import java.time.LocalTime;

@Data
public class HoursOpen {
	private LocalTime open = LocalTime.of(9, 0);
	private LocalTime closed = LocalTime.of(18, 0);

	boolean checkIsValid() {
		if ((open == null && closed != null) || open != null && closed == null) {
			return false;
		}
		return (open == null) || closed.isAfter(open);
	}

	boolean checkIsIntervalBetween(LocalTime from, LocalTime to) {
		return !from.isBefore(open) && !to.isAfter(closed);
	}
}
