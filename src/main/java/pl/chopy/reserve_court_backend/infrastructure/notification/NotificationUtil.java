package pl.chopy.reserve_court_backend.infrastructure.notification;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import pl.chopy.reserve_court_backend.infrastructure.notification.dto.request.NotificationSingleRequest;
import pl.chopy.reserve_court_backend.model.entity.Notification;
import pl.chopy.reserve_court_backend.model.entity.repository.NotificationRepository;

@Component
@AllArgsConstructor
public class NotificationUtil {
	private final RabbitTemplate rabbitTemplate;
	private final NotificationRepository notificationRepository;

	public void sendManagementNotification(Long receiverId, String message) {
		rabbitTemplate.convertAndSend("managementQueue", new NotificationSingleRequest(
				receiverId, message
		));
	}

	public Notification save(Notification notification) {
		return Option.of(notificationRepository.save(notification))
				.getOrElseThrow(
						() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, notification.toString())
				);
	}

	public Notification getById(Long notificationId) {
		return notificationRepository.findById(notificationId)
				.orElseThrow(
						() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found")
				);
	}

}
