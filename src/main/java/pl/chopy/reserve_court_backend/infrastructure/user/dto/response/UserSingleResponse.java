package pl.chopy.reserve_court_backend.infrastructure.user.dto.response;

import lombok.Data;
import pl.chopy.reserve_court_backend.model.entity.User;

import java.time.LocalDate;

@Data
public class UserSingleResponse {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private User.UserRole role;
    private boolean isActive;
    private LocalDate birthDate;
}
