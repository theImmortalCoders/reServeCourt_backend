package pl.chopy.reserve_court_backend.infrastructure.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.chopy.reserve_court_backend.config.ApplicationProps;
import pl.chopy.reserve_court_backend.infrastructure.notification.dto.NotificationMapper;
import pl.chopy.reserve_court_backend.infrastructure.notification.dto.NotificationSingleRequest;
import pl.chopy.reserve_court_backend.infrastructure.user.UserUtil;
import pl.chopy.reserve_court_backend.model.entity.Notification;
import pl.chopy.reserve_court_backend.model.entity.User;
import pl.chopy.reserve_court_backend.model.entity.repository.NotificationRepository;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@AllArgsConstructor
public class NotificationService {
	private final NotificationMapper notificationMapper;
	private final NotificationRepository notificationRepository;
	private final ObjectMapper objectMapper;
	private final ApplicationProps applicationProps;
	private final UserUtil userUtil;

	@RabbitListener(queues = "managementQueue")
	public void listen(NotificationSingleRequest request) throws IOException {
		Notification notification = Option.of(request)
				.map(notificationMapper::map)
				.map(this::save)
				.get();

		send(notification);
	}

	public void markAsRead(Long notificationId) {
		Notification notification = notificationRepository.findById(notificationId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Notification not found"));

		User user = userUtil.getCurrentUser();

		if (!notification.getReceiver().getId().equals(user.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot mark this notification");
		}

		notification.setRead(true);
		notificationRepository.save(notification);
	}

	//

	private Notification save(Notification notification) {
		return Option.of(notificationRepository.save(notification))
				.getOrElseThrow(
						() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, notification.toString())
				);
	}

	private void send(Notification notification) throws IOException {
		URL url = new URL(applicationProps.getBackendDomain() + "/api/socket/send");
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
