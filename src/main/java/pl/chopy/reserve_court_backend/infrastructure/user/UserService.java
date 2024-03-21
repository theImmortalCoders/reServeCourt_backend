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
import pl.chopy.reserve_court_backend.infrastructure.user.dto.request.UserSingleRequest;
import pl.chopy.reserve_court_backend.model.entity.User;
import pl.chopy.reserve_court_backend.model.repository.UserRepository;

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

        return Option.ofOptional(userRepository.findByUsername(username))
                .getOrElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User '" + username + "' not found.")
                );
    }

    //

    UserSingleResponse getCurrentUserResponse() {
        return userMapper.map(getCurrentUser());
    }

    void authenticate(@NotNull UserSingleRequest userRequest, HttpServletRequest request, HttpServletResponse response) {
        Option.ofOptional(userRepository.findByUsername(userRequest.getUsername()))
                .map(admin -> passwordEncoder.matches(userRequest.getPassword(), admin.getHashedPassword()))
                .filter(a -> a)
                .getOrElseThrow(() ->
                        new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials for user '" + userRequest.getUsername() + "'.")
                );

        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(
                userRequest.getUsername(),
                userRequest.getPassword()
        ));
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }

    void register(@NotNull UserSingleRequest request) {
        Option.ofOptional(userRepository.findByUsername(request.getUsername()))
                .map(user -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "User '" + request.getUsername() + "' already exists");
                });

        userRepository.save(new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword())
        ));
    }

    void changePassword(@NotNull UserChangePasswordRequest request) {
        User user = getCurrentUser();

        if (!passwordEncoder.matches(request.getOldPassword(), user.getHashedPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid old password");
        }

        user.setHashedPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    void changeUsername(@NotNull String username, HttpServletRequest request, HttpServletResponse response) {
        User user = userRepository.findByUsername(getCurrentUser().getUsername()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );

        user.setUsername(username);
        userRepository.save(user);

        logoutAdmin(request, response);
    }

    //

    private static void logoutAdmin(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}