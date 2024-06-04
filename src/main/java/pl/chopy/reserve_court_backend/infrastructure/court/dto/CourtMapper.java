package pl.chopy.reserve_court_backend.infrastructure.court.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.chopy.reserve_court_backend.infrastructure.court.dto.response.CourtShortResponse;
import pl.chopy.reserve_court_backend.infrastructure.court.dto.response.CourtSingleResponse;
import pl.chopy.reserve_court_backend.infrastructure.image.dto.ImageMapper;
import pl.chopy.reserve_court_backend.infrastructure.image.dto.ImageMapperImpl;
import pl.chopy.reserve_court_backend.model.entity.Court;

@Mapper
public interface CourtMapper {
	ImageMapper imageMapper = new ImageMapperImpl();

	Court map(CourtSingleRequest request);

	@Mapping(target = "image", expression = "java(" +
			"!court.getImages().isEmpty() ?" +
			"imageMapper.map(court.getImages().get(0)) :" +
			"null)")
	@Mapping(target = "clubId", source = "club.id")
	CourtShortResponse shortMap(Court court);

	@Mapping(target = "clubId", source = "club.id")
	CourtSingleResponse map(Court court);

	void update(@MappingTarget Court court, CourtSingleRequest request);
}
