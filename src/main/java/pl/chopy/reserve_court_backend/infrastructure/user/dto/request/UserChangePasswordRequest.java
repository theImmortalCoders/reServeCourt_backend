package pl.chopy.reserve_court_backend.infrastructure.user.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserChangePasswordRequest {
    private String oldPassword;
    @Size(min = 4, max = 20)
    private String newPassword;
}
