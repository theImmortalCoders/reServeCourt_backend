package pl.chopy.reserve_court_backend.infrastructure.court;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.chopy.reserve_court_backend.infrastructure.court.dto.CourtSingleRequest;
import pl.chopy.reserve_court_backend.infrastructure.court.dto.response.CourtSingleResponse;

@RestController
@Tag(name = "Court", description = "Court workflow")
@RequestMapping("/api/court")
@AllArgsConstructor
public class CourtController {
	private final CourtManageUseCase courtManageUseCase;

	@PostMapping
	@Operation(summary = "Add court to club (Admin)")
	@ApiResponse(responseCode = "200")
	@ApiResponse(responseCode = "401")
	@ApiResponse(responseCode = "403")
	@ApiResponse(responseCode = "400")
	@PreAuthorize("hasAuthority('ADMIN')")
	public void add(@RequestParam Long clubId, @RequestBody CourtSingleRequest request) {
		courtManageUseCase.add(clubId, request);
	}

	@PutMapping("/{courtId}")
	@Operation(summary = "Edit court (Admin)")
	@ApiResponse(responseCode = "200")
	@ApiResponse(responseCode = "401")
	@ApiResponse(responseCode = "403")
	@ApiResponse(responseCode = "400")
	@ApiResponse(responseCode = "404")
	@PreAuthorize("hasAuthority('ADMIN')")
	public void update(@PathVariable Long courtId, @RequestBody CourtSingleRequest request) {
		courtManageUseCase.update(courtId, request);
	}

	@PutMapping("/{courtId}/active")
	@Operation(summary = "Close/unclose court (Admin)")
	@ApiResponse(responseCode = "200")
	@ApiResponse(responseCode = "401")
	@ApiResponse(responseCode = "403")
	@ApiResponse(responseCode = "400")
	@ApiResponse(responseCode = "404")
	@PreAuthorize("hasAuthority('ADMIN')")
	public void toggleClosed(@PathVariable Long courtId, @RequestParam boolean closed) {
		courtManageUseCase.toggleClosed(courtId, closed);
	}

	@DeleteMapping("/{courtId}")
	@Operation(summary = "Delete court (only when no active reservations) (Admin)")
	@ApiResponse(responseCode = "200")
	@ApiResponse(responseCode = "401")
	@ApiResponse(responseCode = "403")
	@ApiResponse(responseCode = "404")
	@ApiResponse(responseCode = "400")
	@PreAuthorize("hasAuthority('ADMIN')")
	public void delete(@PathVariable Long courtId) {
		courtManageUseCase.delete(courtId);
	}

	@GetMapping("/{courtId}")
	@Operation(summary = "Get court details")
	@ApiResponse(responseCode = "200")
	@ApiResponse(responseCode = "404")
	public ResponseEntity<CourtSingleResponse> getDetails(@PathVariable Long courtId) {
		return ResponseEntity.ok(courtManageUseCase.getDetails(courtId));
	}
}
