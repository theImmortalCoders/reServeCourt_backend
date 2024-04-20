package pl.chopy.reserve_court_backend.infrastructure.user.dto.response;

import lombok.Data;

@Data
public class UserShortResponse {
    private Long id;
    private String name;
    private String surname;
    private String email;
}
