package pl.chopy.reserve_court_backend.infrastructure.user.dto;

import org.mapstruct.Mapper;
import pl.chopy.reserve_court_backend.model.entity.User;

@Mapper
public interface UserMapper {
    UserSingleResponse map(User user);

}
