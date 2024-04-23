package pl.chopy.reserve_court_backend.infrastructure.image;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.chopy.reserve_court_backend.infrastructure.image.dto.ImageSingleResponse;

import java.io.IOException;
import java.util.List;

@RestController
@Tag(name = "Image", description = "Image retrieving operations")
@RequestMapping("/api/image")
@AllArgsConstructor
public class ImageController {

	private final ImageService imageService;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "Upload image to server (Auth)", description = "Max file size: 10MB")
	@ApiResponse(responseCode = "200", description = "Image uploaded successfully")
	@ApiResponse(responseCode = "413", description = "Image too large")
	@ApiResponse(responseCode = "401", description = "Unauthorized")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ImageSingleResponse> uploadImage(
			@RequestBody MultipartFile uploadedFile,
			@RequestParam(required = false, defaultValue = "false") Boolean thumbnail
	) throws IOException {
		return ResponseEntity.ok(imageService.uploadImage(uploadedFile, thumbnail));
	}

	@PostMapping(value = "/multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "Upload multiple images to server (Auth)", description = "Max file size: 10MB")
	@ApiResponse(responseCode = "200", description = "Images uploaded successfully")
	@ApiResponse(responseCode = "413", description = "Image too large")
	@ApiResponse(responseCode = "401", description = "Unauthorized")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<ImageSingleResponse>> uploadMultipleImages(
			@RequestParam List<MultipartFile> uploadedFiles,
			@RequestParam(required = false, defaultValue = "false") Boolean thumbnail
	) {
		return ResponseEntity.ok(imageService.uploadMultipleImages(uploadedFiles, thumbnail));
	}

	@GetMapping("{imageId}")
	@Operation(summary = "Download image from server by id", description = "Returns image as byte array")
	@ApiResponse(responseCode = "200", description = "Image downloaded successfully")
	@ApiResponse(responseCode = "404", description = "Image not found")
	public ResponseEntity<byte[]> downloadImage(
			@PathVariable Long imageId,
			@RequestParam(required = false, defaultValue = "false") Boolean thumbnail
	) {
		byte[] imageData = imageService.downloadImage(imageId, thumbnail);
		return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.valueOf("image/png"))
				.body(imageData);
	}

	@GetMapping
	@Operation(summary = "Get all images for user", description = "Returns array of image data")
	@ApiResponse(responseCode = "200", description = "Image downloaded successfully")
	@ApiResponse(responseCode = "404", description = "Image not found")
	public ResponseEntity<List<ImageSingleResponse>> getImagesByUser(
			@RequestParam Long authorId
	) {
		return ResponseEntity.ok(imageService.getImagesIdsByUser(authorId));
	}

	@DeleteMapping("/{imageId}")
	@Operation(summary = "Delete image from server by id (Auth)", description = "Deletes image from server by name")
	@ApiResponse(responseCode = "200", description = "Image deleted successfully")
	@ApiResponse(responseCode = "404", description = "Image not found")
	@ApiResponse(responseCode = "401", description = "Unauthorized")
	@PreAuthorize("isAuthenticated()")
	public void deleteImage(@PathVariable Long imageId) {
		imageService.deleteImage(imageId);
	}
}
