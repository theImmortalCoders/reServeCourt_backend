package pl.chopy.reserve_court_backend.infrastructure.user.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.request.UserSingleRegisterRequest;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.response.UserShortResponse;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.response.UserSingleResponse;
import pl.chopy.reserve_court_backend.model.entity.User;

@Mapper
public interface UserMapper {
	UserSingleResponse map(User user);

	UserShortResponse shortMap(User user);

	@Mapping(target = "hashedPassword", ignore = true)
	@Mapping(target = "active", ignore = true)
	@Mapping(target = "role", ignore = true)
	User map(UserSingleRegisterRequest request);
}
