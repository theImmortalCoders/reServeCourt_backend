package pl.chopy.reserve_court_backend.infrastructure.court;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Court", description = "Court workflow")
@RequestMapping("/api/court")
@AllArgsConstructor
public class CourtController {
}
