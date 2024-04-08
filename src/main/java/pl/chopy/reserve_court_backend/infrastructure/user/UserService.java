package pl.chopy.reserve_court_backend.infrastructure.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.UserMapper;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.UserSingleResponse;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.request.CourtOwnerSingleBecomeRequest;
import pl.chopy.reserve_court_backend.model.UserRole;
import pl.chopy.reserve_court_backend.model.entity.User;
import pl.chopy.reserve_court_backend.model.entity.repository.UserRepository;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserUtil userUtil;
    private final AuthService authService;

    UserSingleResponse getCurrentUserResponse() {
        return userMapper.map(userUtil.getCurrentUser());
    }

    void deleteAccount(HttpServletRequest request, HttpServletResponse response) {
        User user = userUtil.getCurrentUser();
        authService.logoutUser(request, response);

        userRepository.delete(user);
    }

    void changeEmail(@NotNull String email, HttpServletRequest request, HttpServletResponse response) {
        User user = userUtil.getCurrentUser();

        user.setEmail(email);
        userUtil.saveUser(user);

        authService.logoutUser(request, response);
    }

    public void becomeOwner(CourtOwnerSingleBecomeRequest ownerRequest, HttpServletRequest request, HttpServletResponse response) {
        User user = userUtil.getCurrentUser();

        user.setAddress(ownerRequest.getAddress());
        user.setCity(ownerRequest.getCity());
        user.setCompanyName(ownerRequest.getCompanyName());
        user.setRole(UserRole.OWNER);

        userUtil.saveUser(user);

        authService.updateSecurityContext(request, response, user);
    }

    void updateUserRole(Long userId, UserRole newRole) {
        User user = userUtil.getUserById(userId);

        user.setRole(newRole);
        userUtil.saveUser(user);
    }

    void banUser(Long userId) {
        User user = userUtil.getUserById(userId);

        if (user.getId().equals(userUtil.getCurrentUser().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot ban yourself");
        }

        user.setActive(false);
        userUtil.saveUser(user);
    }
}