package pl.chopy.reserve_court_backend.infrastructure.image.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.chopy.reserve_court_backend.model.entity.Image;

@Mapper
public interface ImageMapper {
    @Mapping(target = "authorId", source = "author.id")
    ImageSingleResponse map(Image request);
}
