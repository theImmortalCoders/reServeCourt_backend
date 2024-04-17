package pl.chopy.reserve_court_backend.infrastructure.notification.dto;

import lombok.Data;

@Data
public class NotificationSingleResponse {
	private Long id;
	private String message;
	private Long receiverId;
	private boolean read;
}
