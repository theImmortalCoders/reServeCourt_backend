package pl.chopy.reserve_court_backend.infrastructure.reservation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.chopy.reserve_court_backend.model.entity.Reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReservationTest {
	private final Reservation reservation1 = new Reservation();
	private final Reservation reservation2 = new Reservation();

	@BeforeEach
	public void setUp() {
		reservation1.setTimeFrom(LocalDateTime.of(LocalDate.now(), LocalTime.of(13, 0)));
		reservation1.setTimeTo(LocalDateTime.of(LocalDate.now(), LocalTime.of(14, 0)));
	}

	@Test
	public void shouldValidateHours1() {
		reservation2.setTimeFrom(LocalDateTime.of(LocalDate.now(), LocalTime.of(14, 0)));
		reservation2.setTimeTo(LocalDateTime.of(LocalDate.now(), LocalTime.of(15, 0)));

		assertFalse(reservation1.areReservationsConcurrent(reservation2));
	}

	@Test
	public void shouldValidateHours2() {
		reservation2.setTimeFrom(LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 0)));
		reservation2.setTimeTo(LocalDateTime.of(LocalDate.now(), LocalTime.of(13, 0)));

		assertFalse(reservation1.areReservationsConcurrent(reservation2));
	}

	@Test
	public void shouldValidateHours3() {
		reservation2.setTimeFrom(LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 0)));
		reservation2.setTimeTo(LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 59)));

		assertFalse(reservation1.areReservationsConcurrent(reservation2));
	}

	@Test
	public void shouldValidateHours4() {
		reservation2.setTimeFrom(LocalDateTime.of(LocalDate.now(), LocalTime.of(13, 59)));
		reservation2.setTimeTo(LocalDateTime.of(LocalDate.now(), LocalTime.of(15, 0)));

		assertTrue(reservation1.areReservationsConcurrent(reservation2));
	}

	@Test
	public void shouldValidateHours5() {
		reservation2.setTimeFrom(LocalDateTime.of(LocalDate.now(), LocalTime.of(13, 0)));
		reservation2.setTimeTo(LocalDateTime.of(LocalDate.now(), LocalTime.of(14, 0)));

		assertTrue(reservation1.areReservationsConcurrent(reservation2));
	}

	@Test
	public void shouldValidateHours6() {
		reservation2.setTimeFrom(LocalDateTime.of(LocalDate.now(), LocalTime.of(14, 1)));
		reservation2.setTimeTo(LocalDateTime.of(LocalDate.now(), LocalTime.of(16, 0)));

		assertFalse(reservation1.areReservationsConcurrent(reservation2));
	}

	@Test
	public void shouldValidateHours7() {
		reservation2.setTimeFrom(LocalDateTime.of(LocalDate.now(), LocalTime.of(13, 1)));
		reservation2.setTimeTo(LocalDateTime.of(LocalDate.now(), LocalTime.of(13, 59)));

		assertTrue(reservation1.areReservationsConcurrent(reservation2));
	}

	@Test
	public void shouldValidateHours8() {
		reservation2.setTimeFrom(LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 59)));
		reservation2.setTimeTo(LocalDateTime.of(LocalDate.now(), LocalTime.of(14, 1)));

		assertTrue(reservation1.areReservationsConcurrent(reservation2));
	}
}
