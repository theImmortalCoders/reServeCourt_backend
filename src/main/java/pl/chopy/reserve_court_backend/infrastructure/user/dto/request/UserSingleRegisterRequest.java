package pl.chopy.reserve_court_backend.infrastructure.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSingleRegisterRequest {
    @Size(min = 4, max = 20)
    private String name;
    @Size(min = 4, max = 20)
    private String surname;
    @NotNull
    private LocalDate birthDate;
    @Size(min = 4, max = 20)
    private String password;
    private String phoneNumber;
    @Size(min = 4, max = 30)
    @Email
    private String email;
}
