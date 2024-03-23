package pl.chopy.reserve_court_backend.infrastructure.user;

import io.vavr.control.Option;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
import pl.chopy.reserve_court_backend.infrastructure.user.dto.request.UserChangePasswordRequest;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.request.UserSingleLoginRequest;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.request.UserSingleRegisterRequest;
import pl.chopy.reserve_court_backend.model.UserRole;
import pl.chopy.reserve_court_backend.model.entity.User;
import pl.chopy.reserve_court_backend.model.entity.repository.UserRepository;

import java.time.LocalDate;
import java.time.Period;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder;
    private final SecurityContextHolderStrategy securityContextHolderStrategy;
    private final SecurityContextRepository securityContextRepository;

    public User getCurrentUser() {
        String username = Option.of(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getName)
                .getOrElseThrow(() ->
                        new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated.")
                );

        return Option.ofOptional(userRepository.findByEmail(username))
                .getOrElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User '" + username + "' not found.")
                );
    }

    //

    UserSingleResponse getCurrentUserResponse() {
        return userMapper.map(getCurrentUser());
    }

    void authenticate(@NotNull UserSingleLoginRequest userRequest, HttpServletRequest request, HttpServletResponse response) {
        var user = Option.ofOptional(userRepository.findByEmail(userRequest.getEmail()))
                .filter(u -> passwordEncoder.matches(userRequest.getPassword(), u.getHashedPassword()))
                .getOrElseThrow(() ->
                        new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials for user '" + userRequest.getEmail() + "'.")
                );

        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(
                userRequest.getEmail(),
                user.getHashedPassword(),
                user.getAuthorities()
        ));

        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }

    void register(@NotNull UserSingleRegisterRequest request) {
        Option.ofOptional(userRepository.findByEmail(request.getEmail()))
                .map(user -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "User '" + request.getEmail() + "' already exists");
                });

        if (Period.between(request.getBirthDate(), LocalDate.now()).getYears() < 15) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not have 15 years old");
        }

        userRepository.save(new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getPhoneNumber(),
                request.getName(),
                request.getSurname(),
                request.getBirthDate()
        ));
    }

    public void deleteAccount(HttpServletRequest request, HttpServletResponse response) {
        User user = getCurrentUser();
        logoutAdmin(request, response);

        userRepository.delete(user);
    }

    void changePassword(@NotNull UserChangePasswordRequest request) {
        User user = getCurrentUser();

        if (!passwordEncoder.matches(request.getOldPassword(), user.getHashedPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid old password");
        }

        user.setHashedPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    void changeEmail(@NotNull String email, HttpServletRequest request, HttpServletResponse response) {
        User user = userRepository.findByEmail(getCurrentUser().getUsername()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );

        user.setEmail(email);
        userRepository.save(user);

        logoutAdmin(request, response);
    }

    public void updateRole(Long userId, UserRole newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
                );

        Option.of(user)
                .peek(u -> u.setRole(newRole))
                .map(userRepository::save)
                .getOrElseThrow(
                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role does not exist")
                );
    }

    public void banUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
                );

        if(user.getId().equals(getCurrentUser().getId())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot ban yourself");
        }

        user.setActive(false);
        userRepository.save(user);
    }

    //

    private static void logoutAdmin(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}