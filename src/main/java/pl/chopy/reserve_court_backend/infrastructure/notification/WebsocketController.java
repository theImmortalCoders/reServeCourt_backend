package pl.chopy.reserve_court_backend.infrastructure.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import pl.chopy.reserve_court_backend.infrastructure.notification.dto.NotificationMapper;
import pl.chopy.reserve_court_backend.infrastructure.notification.dto.NotificationSingleRequest;
import pl.chopy.reserve_court_backend.infrastructure.notification.dto.NotificationSingleResponse;
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
	public void send(@RequestBody NotificationSingleRequest notification) throws JsonProcessingException {
		User user = userUtil.getUserById(notification.getReceiverId());

		List<NotificationSingleResponse> notifications = notificationRepository
				.findAllByReceiverIdAndRead(user.getId(), false)
				.stream()
				.map(notificationMapper::map)
				.toList();

		SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
		headerAccessor.setSessionId(user.getSessionId());
		headerAccessor.setLeaveMutable(true);

		simpMessagingTemplate.convertAndSend(
				"/queue/reply",
				objectMapper.writeValueAsString(notifications)
		);

	}

	@MessageMapping("/broadcast")
	public void broadcastMessage(@Header("simpSessionId") String sessionId, Principal principal) throws JsonProcessingException {
		if (principal == null) {
			return;
		}

		User user = userUtil.getUserByEmail(principal.getName());

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

