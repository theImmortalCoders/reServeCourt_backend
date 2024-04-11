package pl.chopy.reserve_court_backend.infrastructure.court.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.chopy.reserve_court_backend.infrastructure.court.dto.response.CourtShortResponse;
import pl.chopy.reserve_court_backend.infrastructure.court.dto.response.CourtSingleResponse;
import pl.chopy.reserve_court_backend.model.entity.Court;

@Mapper
public interface CourtMapper {
    Court map(CourtSingleRequest request);

    @Mapping(target = "image", ignore = true)
    @Mapping(target = "clubId", source = "club.id")
    CourtShortResponse shortMap(Court court);

    @Mapping(target = "clubId", source = "club.id")
    CourtSingleResponse map(Court court);

    void update(@MappingTarget Court court, CourtSingleRequest request);
}
