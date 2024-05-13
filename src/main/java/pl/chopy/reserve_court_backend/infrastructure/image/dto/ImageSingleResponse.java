package pl.chopy.reserve_court_backend.infrastructure.image.dto;

import lombok.Data;

@Data
public class ImageSingleResponse {
	private Long id;
	private String path;
	private boolean hasThumbnail;
	private Long authorId;
}
