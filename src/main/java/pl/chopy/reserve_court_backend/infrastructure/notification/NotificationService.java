package pl.chopy.reserve_court_backend.infrastructure.notification;

import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import pl.chopy.reserve_court_backend.infrastructure.notification.dto.NotificationSingleRequest;

@Service
@AllArgsConstructor
public class NotificationService {

	@RabbitListener(queues = "managementQueue")
	public void listen(NotificationSingleRequest notification) {
		System.out.println("Message read from myQueue : " + notification.getMessage());
	}
}
