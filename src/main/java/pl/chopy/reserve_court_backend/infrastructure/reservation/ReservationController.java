package pl.chopy.reserve_court_backend.infrastructure.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.chopy.reserve_court_backend.infrastructure.reservation.dto.ReservationSingleRequest;
import pl.chopy.reserve_court_backend.infrastructure.reservation.dto.response.ReservationShortResponse;
import pl.chopy.reserve_court_backend.infrastructure.reservation.dto.response.ReservationSingleResponse;

import java.time.LocalDateTime;
import java.util.List;

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

	@DeleteMapping("/{reservationId}")
	@Operation(summary = "Cancel reservation (Logged-in, author or ADMIN only)")
	@ApiResponse(responseCode = "200")
	@ApiResponse(responseCode = "401")
	@ApiResponse(responseCode = "403")
	@ApiResponse(responseCode = "404")
	@ApiResponse(responseCode = "400", description = "Reservation has already been completed")
	@PreAuthorize("isAuthenticated()")
	public void cancel(@PathVariable Long reservationId) {
		reservationService.cancel(reservationId);
	}

	@PatchMapping("/{reservationId}")
	@Operation(summary = "Confirm reservation (Admin)")
	@ApiResponse(responseCode = "200")
	@ApiResponse(responseCode = "401")
	@ApiResponse(responseCode = "403")
	@ApiResponse(responseCode = "404")
	@PreAuthorize("hasAuthority('ADMIN')")
	public void confirm(@PathVariable Long reservationId) {
		reservationService.confirm(reservationId);
	}

	@GetMapping("/{courtId}")
	@Operation(summary = "Get all by court")
	@ApiResponse(responseCode = "200")
	public ResponseEntity<List<ReservationShortResponse>> getByCourtWithFilters(
			@PathVariable Long courtId,
			@RequestParam(required = false) LocalDateTime from,
			@RequestParam(required = false) LocalDateTime to
	) {
		return ResponseEntity.ok(reservationService.getByCourtWithFilters(courtId, from, to));
	}

	@GetMapping("/{clubId}/upcoming")
	@Operation(summary = "Get upcoming by club (Admin)")
	@ApiResponse(responseCode = "200")
	@ApiResponse(responseCode = "401")
	@ApiResponse(responseCode = "403")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<List<ReservationShortResponse>> getUpcomingByClubAndConfirmed(
			@PathVariable Long clubId,
			@RequestParam(required = false) Boolean confirmed
	) {
		return ResponseEntity.ok(reservationService.getUpcomingByClubAndConfirmed(clubId, confirmed));
	}

	@GetMapping("/{reservationId}/details")
	@Operation(summary = "Get reservation details (Logged-in, author or ADMIN only)")
	@ApiResponse(responseCode = "200")
	@ApiResponse(responseCode = "401")
	@ApiResponse(responseCode = "403")
	@ApiResponse(responseCode = "404")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ReservationSingleResponse> getDetails(@PathVariable Long reservationId) {
		return ResponseEntity.ok(reservationService.getDetails(reservationId));
	}
}
