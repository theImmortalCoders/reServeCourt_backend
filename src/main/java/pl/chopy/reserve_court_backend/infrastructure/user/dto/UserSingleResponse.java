package pl.chopy.reserve_court_backend.infrastructure.user.dto;

import lombok.Data;
import pl.chopy.reserve_court_backend.model.UserRole;

import java.time.LocalDate;

@Data
public class UserSingleResponse {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private boolean isActive;
    private LocalDate birthDate;
}
