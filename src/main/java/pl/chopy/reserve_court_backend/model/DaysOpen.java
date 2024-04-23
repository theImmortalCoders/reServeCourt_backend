package pl.chopy.reserve_court_backend.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class DaysOpen {
	private HoursOpen monday = new HoursOpen();
	private HoursOpen tuesday = new HoursOpen();
	private HoursOpen wednesday = new HoursOpen();
	private HoursOpen thursday = new HoursOpen();
	private HoursOpen friday = new HoursOpen();
	private HoursOpen saturday = new HoursOpen();
	private HoursOpen sunday = new HoursOpen();

	public boolean checkIsValid() {
		return monday.checkIsValid() &&
				tuesday.checkIsValid() &&
				wednesday.checkIsValid() &&
				thursday.checkIsValid() &&
				friday.checkIsValid() &&
				saturday.checkIsValid() &&
				sunday.checkIsValid();
	}

	public boolean checkIsDateIntervalInOpeningHours(@NotNull LocalDateTime from, @NotNull LocalDateTime to) {
		if (!sameDay(from, to)) return false;

		LocalTime timeFrom = from.toLocalTime();
		LocalTime timeTo = to.toLocalTime();

		return switch (from.getDayOfWeek()) {
			case MONDAY -> monday.checkIsIntervalBetween(timeFrom, timeTo);
			case TUESDAY -> tuesday.checkIsIntervalBetween(timeFrom, timeTo);
			case WEDNESDAY -> wednesday.checkIsIntervalBetween(timeFrom, timeTo);
			case THURSDAY -> thursday.checkIsIntervalBetween(timeFrom, timeTo);
			case FRIDAY -> friday.checkIsIntervalBetween(timeFrom, timeTo);
			case SATURDAY -> saturday.checkIsIntervalBetween(timeFrom, timeTo);
			case SUNDAY -> sunday.checkIsIntervalBetween(timeFrom, timeTo);
		};
	}

	//

	private static boolean sameDay(LocalDateTime from, LocalDateTime to) {
		return from.toLocalDate().isEqual(to.toLocalDate());
	}
}
