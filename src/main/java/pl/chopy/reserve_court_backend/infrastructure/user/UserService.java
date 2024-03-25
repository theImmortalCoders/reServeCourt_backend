package pl.chopy.reserve_court_backend.infrastructure.user;

import io.vavr.control.Option;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.UserMapper;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.UserSingleResponse;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.request.CourtOwnerSingleBecomeRequest;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.request.UserChangePasswordRequest;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.request.UserSingleLoginRequest;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.request.UserSingleRegisterRequest;
import pl.chopy.reserve_court_backend.model.UserRole;
import pl.chopy.reserve_court_backend.model.entity.User;
import pl.chopy.reserve_court_backend.model.entity.repository.UserRepository;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserUtil userUtil;

    private final BCryptPasswordEncoder passwordEncoder;
    private final SecurityContextHolderStrategy securityContextHolderStrategy;
    private final SecurityContextRepository securityContextRepository;

    UserSingleResponse getCurrentUserResponse() {
        return userMapper.map(userUtil.getCurrentUser());
    }

    void authenticate(@NotNull UserSingleLoginRequest userRequest, HttpServletRequest request, HttpServletResponse response) {
        User user = Option.ofOptional(getOptionalByEmail(userRequest.getEmail()))
                .filter(u -> passwordEncoder.matches(userRequest.getPassword(), u.getHashedPassword()))
                .getOrElseThrow(() ->
                        new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials for user '" + userRequest.getEmail() + "'.")
                );

        updateSecurityContext(request, response, user);
    }

    void register(@NotNull UserSingleRegisterRequest request) {
        Option.ofOptional(getOptionalByEmail(request.getEmail()))
                .map(user -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "User '" + request.getEmail() + "' already exists");
                });

        if (Period.between(request.getBirthDate(), LocalDate.now()).getYears() < 15) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not have 15 years old");
        }

        User user = userMapper.map(request);
        user.setHashedPassword(passwordEncoder.encode(request.getPassword()));

        userUtil.saveUser(user);
    }

    void deleteAccount(HttpServletRequest request, HttpServletResponse response) {
        User user = userUtil.getCurrentUser();
        logoutAdmin(request, response);

        userRepository.delete(user);
    }

    void changePassword(@NotNull UserChangePasswordRequest request) {
        User user = userUtil.getCurrentUser();

        if (!passwordEncoder.matches(request.getOldPassword(), user.getHashedPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid old password");
        }

        user.setHashedPassword(passwordEncoder.encode(request.getNewPassword()));
        userUtil.saveUser(user);
    }

    void changeEmail(@NotNull String email, HttpServletRequest request, HttpServletResponse response) {
        User user = userUtil.getCurrentUser();

        user.setEmail(email);
        userUtil.saveUser(user);

        logoutAdmin(request, response);
    }

    public void becomeOwner(CourtOwnerSingleBecomeRequest ownerRequest, HttpServletRequest request, HttpServletResponse response) {
        User user = userUtil.getCurrentUser();

        user.setAddress(ownerRequest.getAddress());
        user.setCity(ownerRequest.getCity());
        user.setCompanyName(ownerRequest.getCompanyName());
        user.setRole(UserRole.OWNER);

        userUtil.saveUser(user);

        updateSecurityContext(request, response, user);
    }

    void updateUserRole(Long userId, UserRole newRole) {
        User user = getUserById(userId);

        user.setRole(newRole);
        userUtil.saveUser(user);
    }

    void banUser(Long userId) {
        User user = getUserById(userId);

        if (user.getId().equals(userUtil.getCurrentUser().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot ban yourself");
        }

        user.setActive(false);
        userUtil.saveUser(user);
    }

    //

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
                );
    }

    private void updateSecurityContext(HttpServletRequest request, HttpServletResponse response, User user) {
        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                user.getHashedPassword(),
                user.getAuthorities()
        ));

        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }

    private Optional<User> getOptionalByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    private void logoutAdmin(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}