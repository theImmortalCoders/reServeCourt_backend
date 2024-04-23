package pl.chopy.reserve_court_backend.infrastructure.image;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import pl.chopy.reserve_court_backend.infrastructure.image.dto.ImageMapper;
import pl.chopy.reserve_court_backend.infrastructure.image.dto.ImageSingleResponse;
import pl.chopy.reserve_court_backend.infrastructure.user.UserUtil;
import pl.chopy.reserve_court_backend.model.entity.Image;
import pl.chopy.reserve_court_backend.model.entity.User;
import pl.chopy.reserve_court_backend.model.entity.repository.ImageRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ImageService {
	private final String path = "/var/lib/postgresql/data";
	private final ImageRepository imageRepository;
	private final ImageMapper imageMapper;
	private final UserUtil userUtil;
	private final ImageUtil imageUtil;

	ImageSingleResponse uploadImage(@NotNull MultipartFile imageFile, Boolean thumbnail) throws IOException {
		String uniqueFileName = LocalDateTime.now()
				.toString()
				.replaceAll(":", "_") + "_"
				+ imageFile.getOriginalFilename();

		Path uploadPath = Paths.get(path);
		Path filePath = uploadPath.resolve(uniqueFileName);

		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}

		Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

		Image image = new Image();
		image.setAuthor(userUtil.getCurrentUser());
		image.setPath(uniqueFileName);

		if (thumbnail) {
			String thumbnailFileName = "thumbnail_" + uniqueFileName;
			Path thumbnailFilePath = uploadPath.resolve(thumbnailFileName);
			BufferedImage inputImage = ImageIO.read(filePath.toFile());
			createThumbnail(filePath, thumbnailFilePath, inputImage.getWidth() / 3, inputImage.getHeight() / 3);
			image.setHasThumbnail(true);
		}

		return imageMapper.map(saveImage(image));
	}

	List<ImageSingleResponse> uploadMultipleImages(@NotNull List<MultipartFile> imageFiles, Boolean thumbnail) {
		List<ImageSingleResponse> imagesResponses = new ArrayList<>();

		for (MultipartFile imageFile : imageFiles) {
			try {
				imagesResponses.add(uploadImage(imageFile, thumbnail));
			} catch (IOException e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request:" + imageFile);
			}
		}

		return imagesResponses;
	}

	byte[] downloadImage(Long imageId, @NotNull Boolean thumbnail) {
		String imageName = imageUtil.getImageById(imageId).getPath();

		if (thumbnail) {
			imageName = "thumbnail_" + imageName;
		}

		Path imagePath = Paths.get(path, imageName);

		try {
			return Files.readAllBytes(imagePath);
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image '" + imageName + "' not found");
		}
	}

	List<ImageSingleResponse> getImagesIdsByUser(Long authorId) {
		User user = userUtil.getUserById(authorId);
		return imageRepository.findAllByAuthor(user)
				.stream()
				.map(imageMapper::map)
				.toList();
	}

	void deleteImage(Long imageId) {
		User user = userUtil.getCurrentUser();
		Image image = imageUtil.getImageById(imageId);

		if (!user.getId().equals(image.getAuthor().getId()) && !user.getRole().equals(User.UserRole.ADMIN)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot delete image you not own");
		}

		Path imagePath = Paths.get(path, image.getPath());
		try {
			Files.delete(imagePath);
			imageRepository.delete(image);
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image '" + imageId + "' not found");
		}
	}

	//

	private Image saveImage(Image image) {
		return Option.of(imageRepository.save(image))
				.getOrElseThrow(
						() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, image.toString())
				);
	}

	private void createThumbnail(@NotNull Path sourcePath, @NotNull Path targetPath, int width, int height) throws IOException {
		BufferedImage originalImage = ImageIO.read(sourcePath.toFile());
		BufferedImage resizedImage = new BufferedImage(width, height, originalImage.getType());
		Graphics2D g = resizedImage.createGraphics();

		g.drawImage(originalImage, 0, 0, width, height, null);
		g.dispose();

		ImageIO.write(resizedImage, "png", targetPath.toFile());
	}
}
