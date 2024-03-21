package pl.chopy.reserve_court_backend.infrastructure.user.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSingleRequest {
    @Size(min = 4, max = 20)
    String username;
    @Size(min = 4, max = 20)
    String password;
}
