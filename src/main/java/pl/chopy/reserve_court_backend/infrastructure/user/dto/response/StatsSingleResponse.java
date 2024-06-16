package pl.chopy.reserve_court_backend.infrastructure.user.dto.response;

import lombok.Data;

@Data
public class StatsSingleResponse {
	private int clubsAmount;
	private int outdoorCourtsAmount;
	private int indoorCourtsAmount;
	private int reservationsAmount;
}
