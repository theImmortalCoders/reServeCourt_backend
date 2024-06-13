package pl.chopy.reserve_court_backend.infrastructure.notification.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSingleRequest {
	private Long receiverId;
	private String message;
}
