package pl.chopy.reserve_court_backend.infrastructure.court;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.chopy.reserve_court_backend.infrastructure.court.dto.CourtSingleRequest;

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
}
