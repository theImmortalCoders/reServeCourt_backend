package pl.chopy.reserve_court_backend.infrastructure.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.chopy.reserve_court_backend.infrastructure.reservation.dto.ReservationSingleRequest;
import pl.chopy.reserve_court_backend.infrastructure.reservation.dto.response.ReservationSingleResponse;

@RestController
@Tag(name = "Reservation", description = "Court reservation workflow")
@RequestMapping("/api/reservation")
@AllArgsConstructor
public class ReservationController {
	private final ReservationService reservationService;

	@PostMapping
	@Operation(summary = "Add reservation (Logged-in)")
	@ApiResponse(responseCode = "200")
	@ApiResponse(responseCode = "401")
	@ApiResponse(responseCode = "400", description = "Various messages available")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ReservationSingleResponse> reserve(@RequestBody ReservationSingleRequest request, @RequestParam Long courtId) {
		return ResponseEntity.ok(reservationService.reserve(request, courtId));
	}

	@PutMapping("/{reservationId}")
	@Operation(summary = "Edit reservation (Logged-in, author only)")
	@ApiResponse(responseCode = "200")
	@ApiResponse(responseCode = "401")
	@ApiResponse(responseCode = "403")
	@ApiResponse(responseCode = "404")
	@ApiResponse(responseCode = "400", description = "Various messages available")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ReservationSingleResponse> update(@RequestBody ReservationSingleRequest request, @PathVariable Long reservationId) {
		return ResponseEntity.ok(reservationService.update(request, reservationId));
	}


}
