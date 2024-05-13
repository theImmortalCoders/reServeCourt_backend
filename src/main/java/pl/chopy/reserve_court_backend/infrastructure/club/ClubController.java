package pl.chopy.reserve_court_backend.infrastructure.club;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.chopy.reserve_court_backend.infrastructure.club.dto.ClubSingleRequest;
import pl.chopy.reserve_court_backend.infrastructure.club.dto.response.ClubShortResponse;
import pl.chopy.reserve_court_backend.infrastructure.club.dto.response.ClubSingleResponse;

@RestController
@Tag(name = "Club", description = "Club workflow")
@RequestMapping("/api/club")
@AllArgsConstructor
public class ClubController {
	private final ClubService clubService;

	@PostMapping
	@Operation(summary = "Add club (Admin)")
	@ApiResponse(responseCode = "200")
	@ApiResponse(responseCode = "401")
	@ApiResponse(responseCode = "403")
	@ApiResponse(responseCode = "400")
	@PreAuthorize("hasAuthority('ADMIN')")
	public void add(@RequestBody ClubSingleRequest request) {
		clubService.add(request);
	}

	@PutMapping("/{clubId}")
	@Operation(summary = "Update club (Admin)")
	@ApiResponse(responseCode = "200")
	@ApiResponse(responseCode = "401")
	@ApiResponse(responseCode = "403")
	@ApiResponse(responseCode = "400")
	@ApiResponse(responseCode = "404")
	@PreAuthorize("hasAuthority('ADMIN')")
	public void update(@PathVariable Long clubId, @RequestBody ClubSingleRequest request) {
		clubService.update(clubId, request);
	}

	@PostMapping("/{clubId}/rate")
	@Operation(summary = "Rate club (Login)")
	@ApiResponse(responseCode = "200")
	@ApiResponse(responseCode = "401")
	@ApiResponse(responseCode = "400", description = "Rating must be between 0 and 5.0")
	@ApiResponse(responseCode = "404")
	@PreAuthorize("isAuthenticated()")
	public void rate(@PathVariable Long clubId, @RequestParam double rate) {
		clubService.rate(clubId, rate);
	}

	@DeleteMapping("/{clubId}")
	@Operation(summary = "Delete club (Admin)", description = "Only when no active reservations")
	@ApiResponse(responseCode = "200")
	@ApiResponse(responseCode = "401")
	@ApiResponse(responseCode = "403")
	@ApiResponse(responseCode = "400", description = "Club has active reservations")
	@ApiResponse(responseCode = "404")
	@PreAuthorize("hasAuthority('ADMIN')")
	public void delete(@PathVariable Long clubId) {
		clubService.delete(clubId);
	}

	@GetMapping
	@Operation(summary = "Get all clubs with sorting and filter")
	@ApiResponse(responseCode = "200")
	public ResponseEntity<Page<ClubShortResponse>> getAll(
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "id") String sort,
			@RequestParam(defaultValue = "DESC") Sort.Direction sortDirection,
			@RequestParam(required = false) String ownerName,
			@RequestParam(required = false) String name
	) {
		var pageRequest = PageRequest.of(page, size, sortDirection, sort);
		return ResponseEntity.ok(clubService.getAll(pageRequest, ownerName, name));
	}

	@GetMapping("/{clubId}")
	@Operation(summary = "Get club details", description = "With role checking")
	@ApiResponse(responseCode = "200")
	@ApiResponse(responseCode = "404")
	public ResponseEntity<ClubSingleResponse> getDetails(@PathVariable Long clubId) {
		return ResponseEntity.ok(clubService.getDetails(clubId));
	}

}
