package pl.chopy.reserve_court_backend.infrastructure.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.chopy.reserve_court_backend.infrastructure.notification.dto.NotificationMapper;
import pl.chopy.reserve_court_backend.infrastructure.notification.dto.NotificationSingleRequest;
import pl.chopy.reserve_court_backend.model.entity.Notification;
import pl.chopy.reserve_court_backend.model.entity.repository.NotificationRepository;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@Service
@AllArgsConstructor
public class NotificationService {
	private final NotificationMapper notificationMapper;
	private final NotificationRepository notificationRepository;
	private final ObjectMapper objectMapper;

	@RabbitListener(queues = "managementQueue")
	public void listen(NotificationSingleRequest request) throws IOException {
		Notification notification = Option.of(request)
				.map(notificationMapper::map)
				.map(this::save)
				.get();

		send(notification);
	}

	//

	private Notification save(Notification notification) {
		return Option.of(notificationRepository.save(notification))
				.getOrElseThrow(
						() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, notification.toString())
				);
	}

	private void send(Notification notification) throws IOException {
		URL url = new URL("http://localhost:8080/api/socket/send");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		con.setDoOutput(true);

		var request = new NotificationSingleRequest(notification.getReceiver().getId(), notification.getMessage());

		String jsonNotification = objectMapper.writeValueAsString(request);
		try (OutputStream os = con.getOutputStream()) {
			byte[] input = jsonNotification.getBytes("utf-8");
			os.write(input, 0, input.length);
		}
		int responseCode = con.getResponseCode();
		System.out.println("Response Code: " + responseCode);
	}
}
