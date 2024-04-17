package pl.chopy.reserve_court_backend.infrastructure.notification.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.chopy.reserve_court_backend.infrastructure.user.UserUtil;
import pl.chopy.reserve_court_backend.model.entity.Notification;

@Component
@AllArgsConstructor
public class NotificationMapper {
	private final UserUtil userUtil;

	public Notification map(NotificationSingleRequest request) {
		var notification = new Notification();
		notification.setMessage(request.getMessage());
		notification.setReceiver(userUtil.getUserById(request.getReceiverId()));
		return notification;
	}

	public NotificationSingleResponse map(Notification notification) {
		var response = new NotificationSingleResponse();
		response.setId(notification.getId());
		response.setRead(notification.isRead());
		response.setReceiverId(notification.getReceiver().getId());
		response.setMessage(notification.getMessage());
		return response;
	}
}
