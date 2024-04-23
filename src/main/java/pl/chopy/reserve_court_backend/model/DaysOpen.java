package pl.chopy.reserve_court_backend.model;

import lombok.Data;

@Data
public class DaysOpen {
	private HoursOpen monday = new HoursOpen();
	private HoursOpen tuesday = new HoursOpen();
	private HoursOpen wednesday = new HoursOpen();
	private HoursOpen thursday = new HoursOpen();
	private HoursOpen friday = new HoursOpen();
	private HoursOpen saturday = new HoursOpen();
	private HoursOpen sunday = new HoursOpen();

	public boolean checkValid() {
		return monday.checkValid() &&
				tuesday.checkValid() &&
				wednesday.checkValid() &&
				thursday.checkValid() &&
				friday.checkValid() &&
				saturday.checkValid() &&
				sunday.checkValid();
	}
}
