package pl.chopy.reserve_court_backend.infrastructure.notification.dto;

import lombok.Data;

@Data
public class NotificationSocketRequest {
	private Long notificationId;
	private Long receiverId;
}
