package pl.chopy.reserve_court_backend.infrastructure.reservation;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Reservation", description = "Court reservation workflow")
@RequestMapping("/api/reservation")
@AllArgsConstructor
public class ReservationController {
	private final ReservationService reservationService;

}
