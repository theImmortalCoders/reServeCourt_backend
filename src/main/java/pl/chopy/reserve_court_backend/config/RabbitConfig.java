package pl.chopy.reserve_court_backend.config;

import lombok.AllArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class RabbitConfig {
	@Bean
	public Queue myQueue() {
		return new Queue("myQueue", false);
	}
}
