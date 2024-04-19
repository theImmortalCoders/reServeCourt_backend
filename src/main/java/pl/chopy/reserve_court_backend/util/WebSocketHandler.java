package pl.chopy.reserve_court_backend.util;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

public class WebSocketHandler extends TextWebSocketHandler {
	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws IOException {
		String receivedMessage = (String) message.getPayload();
		session.sendMessage(new TextMessage("Received: " + receivedMessage));
		System.out.println("elo");
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {

	}

}