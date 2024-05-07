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
import pl.chopy.reserve_court_backend.infrastructure.notification.dto.NotificationSocketRequest;
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
	private final NotificationUtil notificationUtil;

	@RabbitListener(queues = "managementQueue")
	public void listen(NotificationSingleRequest request) throws IOException {
		Notification notification = Option.of(request)
				.map(notificationMapper::map)
				.map(notificationUtil::save)
				.get();

		send(notification);
	}

	public void markAsRead(Long notificationId) {
		Notification notification = notificationUtil.getById(notificationId);

		User user = userUtil.getCurrentUser();

		if (!notification.getReceiver().getId().equals(user.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot mark this notification");
		}

		notification.setRead(true);
		notificationRepository.save(notification);
	}

	//

	private void send(Notification notification) throws IOException {
		URL url = new URL(applicationProps.getBackendDomain() + "/api/socket/send");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		con.setDoOutput(true);

		NotificationSocketRequest request = new NotificationSocketRequest();
		request.setNotificationId(notification.getId());
		request.setReceiverId(notification.getReceiver().getId());

		String notificationRequestJSON = objectMapper.writeValueAsString(request);
		try (OutputStream os = con.getOutputStream()) {
			byte[] input = notificationRequestJSON.getBytes("utf-8");
			os.write(input, 0, input.length);
		}
		int responseCode = con.getResponseCode();
		System.out.println("Response Code: " + responseCode);
	}
}
