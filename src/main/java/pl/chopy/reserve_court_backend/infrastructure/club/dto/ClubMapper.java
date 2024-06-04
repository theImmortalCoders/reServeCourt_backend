package pl.chopy.reserve_court_backend.infrastructure.club.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.chopy.reserve_court_backend.infrastructure.club.dto.response.ClubShortResponse;
import pl.chopy.reserve_court_backend.infrastructure.club.dto.response.ClubSingleResponse;
import pl.chopy.reserve_court_backend.infrastructure.court.dto.CourtMapper;
import pl.chopy.reserve_court_backend.infrastructure.court.dto.CourtMapperImpl;
import pl.chopy.reserve_court_backend.infrastructure.image.dto.ImageMapper;
import pl.chopy.reserve_court_backend.infrastructure.image.dto.ImageMapperImpl;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.UserMapper;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.UserMapperImpl;
import pl.chopy.reserve_court_backend.model.entity.Club;
import pl.chopy.reserve_court_backend.model.entity.Rate;

@Mapper
public interface ClubMapper {
	@Mapping(target = "logo", ignore = true)
	Club map(ClubSingleRequest request);

	default ClubShortResponse shortMap(Club request) {
		ImageMapper imageMapper = new ImageMapperImpl();

		var response = new ClubShortResponse();
		response.setId(request.getId());
		response.setName(request.getName());
		response.setDescription(request.getDescription());
		response.setLogo(imageMapper.map(request.getLogo()));

		double avg = request.getRates().stream().mapToDouble(Rate::getValue).average().orElse(0);
		response.setRating(avg);
		response.setCourtsAmount(request.getCourts().size());
		response.setLocation(request.getLocation());

		return response;
	}

	default ClubSingleResponse map(Club request) {
		ImageMapper imageMapper = new ImageMapperImpl();
		CourtMapper courtMapper = new CourtMapperImpl();
		UserMapper userMapper = new UserMapperImpl();

		var response = new ClubSingleResponse();
		response.setId(request.getId());
		response.setName(request.getName());
		response.setDescription(request.getDescription());
		response.setLocation(request.getLocation());
		double avg = request.getRates().stream().mapToDouble(Rate::getValue).average().orElse(0);
		response.setRating(avg);
		response.setLogo(imageMapper.map(request.getLogo()));
		response.setCourts(request.getCourts()
				.stream()
				.map(c -> {
					var courtResponse = courtMapper.shortMap(c);
					if (!c.getImages().isEmpty()) {
						courtResponse.setImage(imageMapper.map(
								c.getImages().get(0)
						));
					}
					return courtResponse;
				})
				.toList()
		);
		response.setOwner(userMapper.shortMap(request.getOwner()));
		response.setDaysOpen(request.getDaysOpen());

		return response;
	}
}
