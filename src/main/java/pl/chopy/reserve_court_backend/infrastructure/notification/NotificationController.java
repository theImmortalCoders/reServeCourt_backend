package pl.chopy.reserve_court_backend.infrastructure.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Notification", description = "Notification workflow")
@RequestMapping("/api/notification")
@AllArgsConstructor
public class NotificationController {
	private final NotificationService notificationService;

	@PostMapping
	@Operation(summary = "Mark notification as read")
	@ApiResponse(responseCode = "200")
	@ApiResponse(responseCode = "401")
	@ApiResponse(responseCode = "403")
	@PreAuthorize("isAuthenticated()")
	public void markAsRead(@RequestParam Long notificationId) {
		notificationService.markAsRead(notificationId);
	}

}
