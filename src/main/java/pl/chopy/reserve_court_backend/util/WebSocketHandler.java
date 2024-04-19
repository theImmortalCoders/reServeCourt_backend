package pl.chopy.reserve_court_backend.util;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import pl.chopy.reserve_court_backend.model.entity.repository.NotificationRepository;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@AllArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {
	private final NotificationRepository notificationRepository;
	private final ObjectMapper objectMapper;

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws IOException {
		String receivedMessage = (String) message.getPayload();
		String jsonData = objectMapper.writeValueAsString(notificationRepository.findAll());
		session.sendMessage(new TextMessage(jsonData));
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws IOException {
	}
}
