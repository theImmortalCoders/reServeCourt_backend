package pl.chopy.reserve_court_backend.infrastructure.notification;

import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotificationService {
	@RabbitListener(queues = "myQueue")
	public void listen(String in) {
		System.out.println("Message read from myQueue : " + in);
	}
}
