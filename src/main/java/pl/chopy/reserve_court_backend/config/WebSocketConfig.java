package pl.chopy.reserve_court_backend.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker
@AllArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	private final ApplicationProps applicationProps;

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/topic", "/queue");
		config.setApplicationDestinationPrefixes("/app");
		config.setUserDestinationPrefix("/user");
	}

//	@Bean
//	public RmeSessionChannelInterceptor rmeSessionChannelInterceptor() {
//		return new RmeSessionChannelInterceptor();
//	}
//
//	@Override
//	public void configureClientInboundChannel(ChannelRegistration registration) {
//		registration.interceptors(rmeSessionChannelInterceptor());
//	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws-endpoint").setAllowedOrigins(applicationProps.getSocketDomain()).withSockJS();
	}
}