package pl.chopy.reserve_court_backend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import pl.chopy.reserve_court_backend.infrastructure.notification.dto.NotificationMapper;
import pl.chopy.reserve_court_backend.infrastructure.notification.dto.NotificationSingleResponse;
import pl.chopy.reserve_court_backend.model.entity.repository.NotificationRepository;

import java.util.List;

@AllArgsConstructor
@Controller
public class WebSocketHandler {

	private final NotificationRepository notificationRepository;
	private final NotificationMapper notificationMapper;
	private final SimpMessagingTemplate simpMessagingTemplate;
	private final ObjectMapper objectMapper;

	@MessageMapping("/broadcast")
	public void broadcastMessage() {
		List<NotificationSingleResponse> notifications = notificationRepository
				.findAll()
				.stream()
				.map(notificationMapper::map)
				.toList();

		try {
			simpMessagingTemplate.convertAndSend("/topic/reply", objectMapper.writeValueAsString(notifications));
		} catch (JsonProcessingException ignored) {
		}
		System.out.println(notifications);
	}
}

