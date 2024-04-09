package pl.chopy.reserve_court_backend.infrastructure.club;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Club", description = "Club workflow")
@RequestMapping("/api/club")
@AllArgsConstructor
public class ClubController {
}
