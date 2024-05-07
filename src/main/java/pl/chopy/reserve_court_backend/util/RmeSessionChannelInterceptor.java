package pl.chopy.reserve_court_backend.util;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;

public class RmeSessionChannelInterceptor implements ChannelInterceptor {

	public static String sessionId;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		sessionId = (String) message.getHeaders().get("simpSessionId");
		System.out.println("simpSessionId: " + sessionId);
		return ChannelInterceptor.super.preSend(message, channel);
	}
}
