package pl.chopy.reserve_court_backend.infrastructure.notification;

import io.vavr.control.Option;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.chopy.reserve_court_backend.infrastructure.notification.dto.NotificationMapper;
import pl.chopy.reserve_court_backend.infrastructure.notification.dto.NotificationSingleRequest;
import pl.chopy.reserve_court_backend.model.entity.Notification;
import pl.chopy.reserve_court_backend.model.entity.repository.NotificationRepository;
import pl.chopy.reserve_court_backend.util.WebSocketHandler;

@Service
@AllArgsConstructor
public class NotificationService {
	private final NotificationMapper notificationMapper;
	private final NotificationRepository notificationRepository;
	private final WebSocketHandler webSocketHandler;

	@RabbitListener(queues = "managementQueue")
	public void listen(NotificationSingleRequest request) {
		Option.of(request)
				.map(notificationMapper::map)
				.peek(this::save);

		webSocketHandler.broadcastMessage();
	}

	//

	private void save(Notification notification) {
		Option.of(notificationRepository.save(notification))
				.getOrElseThrow(
						() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, notification.toString())
				);
	}
}
