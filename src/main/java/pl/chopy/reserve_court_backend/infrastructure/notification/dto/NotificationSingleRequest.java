package pl.chopy.reserve_court_backend.infrastructure.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class NotificationSingleRequest {
	private List<Long> receiversIds;
	private String message;
}
