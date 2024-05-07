package pl.chopy.reserve_court_backend.infrastructure.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pl.chopy.reserve_court_backend.infrastructure.notification.dto.NotificationMapper;
import pl.chopy.reserve_court_backend.infrastructure.notification.dto.NotificationSingleRequest;
import pl.chopy.reserve_court_backend.infrastructure.notification.dto.NotificationSingleResponse;
import pl.chopy.reserve_court_backend.infrastructure.notification.dto.NotificationSocketRequest;
import pl.chopy.reserve_court_backend.infrastructure.user.UserUtil;
import pl.chopy.reserve_court_backend.model.entity.Notification;
import pl.chopy.reserve_court_backend.model.entity.User;
import pl.chopy.reserve_court_backend.model.entity.repository.NotificationRepository;

import java.security.Principal;
import java.util.List;

@AllArgsConstructor
@RestController
public class WebsocketController {
	private final UserUtil userUtil;
	private final NotificationRepository notificationRepository;
	private final NotificationMapper notificationMapper;
	private final SimpMessagingTemplate simpMessagingTemplate;
	private final ObjectMapper objectMapper;

	@PostMapping("/api/socket/send")
	public void send(@RequestBody NotificationSocketRequest request) throws JsonProcessingException {
		User user = userUtil.getUserById(request.getReceiverId());
		String sessionId = user.getSessionId();
		SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
		headerAccessor.setSessionId(sessionId);
		headerAccessor.setLeaveMutable(true);

		Notification notification = notificationRepository.findById(request.getNotificationId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Notification not found"));

		simpMessagingTemplate.convertAndSendToUser(
				sessionId,
				"/queue/reply",
				objectMapper.writeValueAsString(List.of(notificationMapper.map(notification))),
				headerAccessor.getMessageHeaders());
	}

	@MessageMapping("/broadcast")
	public void broadcastMessage(@Header("simpSessionId") String sessionId, Principal principal) throws JsonProcessingException {
		if (principal == null) {
			return;
		}
		User user = userUtil.getUserByEmail(principal.getName());
		user.setSessionId(sessionId);
		userUtil.saveUser(user);
		sendNotifications(user, sessionId);
	}

	private void sendNotifications(User user, String sessionId) throws JsonProcessingException {
		List<NotificationSingleResponse> notifications = notificationRepository
				.findAllByReceiverIdAndRead(user.getId(), false)
				.stream()
				.map(notificationMapper::map)
				.toList();

		SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
		headerAccessor.setSessionId(sessionId);
		headerAccessor.setLeaveMutable(true);
		simpMessagingTemplate.convertAndSendToUser(
				sessionId,
				"/queue/reply",
				objectMapper.writeValueAsString(notifications),
				headerAccessor.getMessageHeaders());
	}
}

