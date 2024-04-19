package pl.chopy.reserve_court_backend.infrastructure.notification;

import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pl.chopy.reserve_court_backend.infrastructure.notification.dto.NotificationMapper;
import pl.chopy.reserve_court_backend.infrastructure.notification.dto.NotificationSingleRequest;
import pl.chopy.reserve_court_backend.infrastructure.notification.dto.NotificationSingleResponse;
import pl.chopy.reserve_court_backend.model.entity.repository.NotificationRepository;
import pl.chopy.reserve_court_backend.util.WebSocketHandler;

import java.util.List;

@Component
@AllArgsConstructor
public class NotificationUtil {
	private final RabbitTemplate rabbitTemplate;

	public void sendManagementNotification(Long receiverId, String message) {
		rabbitTemplate.convertAndSend("managementQueue", new NotificationSingleRequest(
				receiverId, message
		));
	}

}
