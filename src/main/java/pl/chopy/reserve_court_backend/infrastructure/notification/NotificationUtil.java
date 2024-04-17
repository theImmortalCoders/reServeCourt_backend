package pl.chopy.reserve_court_backend.infrastructure.notification;

import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pl.chopy.reserve_court_backend.infrastructure.notification.dto.NotificationSingleRequest;

import java.util.List;

@Component
@AllArgsConstructor
public class NotificationUtil {
	private final RabbitTemplate rabbitTemplate;

	public void sendManagementNotification(List<Long> receiversIds, String message) {
		rabbitTemplate.convertAndSend("managementQueue", new NotificationSingleRequest(
				receiversIds, message
		));
	}
}
