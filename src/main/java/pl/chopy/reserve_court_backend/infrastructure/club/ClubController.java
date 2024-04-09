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

    @DeleteMapping("/{clubId}")
    @Operation(summary = "Delete club (Admin)", description = "Only when no active reservations")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "401")
    @ApiResponse(responseCode = "403")
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
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxRating
    ) {
        var pageRequest = PageRequest.of(page, size, sortDirection, sort);
        return ResponseEntity.ok(clubService.getAll(pageRequest, ownerName, name, minRating, maxRating));
    }

    @GetMapping("/{clubId}")
    @Operation(summary = "Get club details", description = "With role checking")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404")
    public ResponseEntity<ClubSingleResponse> getDetails(@PathVariable Long clubId) {
        return ResponseEntity.ok(clubService.getDetails(clubId));
    }

}
