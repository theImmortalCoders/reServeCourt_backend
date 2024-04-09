package pl.chopy.reserve_court_backend.infrastructure.club.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.chopy.reserve_court_backend.infrastructure.club.dto.response.ClubShortResponse;
import pl.chopy.reserve_court_backend.model.entity.Club;

@Mapper
public interface ClubMapper {
    @Mapping(target = "logo", ignore = true)
    Club map(ClubSingleRequest request);

    ClubShortResponse shortMap(Club request);
}
