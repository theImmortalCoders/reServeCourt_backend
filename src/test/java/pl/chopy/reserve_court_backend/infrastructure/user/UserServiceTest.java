package pl.chopy.reserve_court_backend.infrastructure.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.server.ResponseStatusException;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.UserMapper;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.UserMapperImpl;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.UserSingleResponse;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.request.UserChangePasswordRequest;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.request.UserSingleRequest;
import pl.chopy.reserve_court_backend.model.entity.User;
import pl.chopy.reserve_court_backend.model.entity.repository.UserRepository;
import pl.chopy.reserve_court_backend.util.StringAsJSON;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTest {
    private User user;
    private UserSingleResponse userSingleResponse;
    private UserSingleRequest userSingleRequest;

    @Spy
    private final UserMapper userMapper = new UserMapperImpl();
    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SecurityContextHolderStrategy securityContextHolderStrategy;
    @Mock
    private SecurityContextRepository securityContextRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContextHolderStrategy.createEmptyContext()).thenReturn(securityContext);

        user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setHashedPassword(passwordEncoder.encode("user"));

        userSingleRequest = new UserSingleRequest();
        userSingleRequest.setUsername("user");
        userSingleRequest.setPassword("user");

        userSingleResponse = new UserSingleResponse();
        userSingleResponse.setId(1L);
        userSingleResponse.setUsername("user");
    }

    @Test
    public void shouldGetCurrentUser() {
        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.of(user));
        when(SecurityContextHolder.getContext().getAuthentication().getName())
                .thenReturn("user");

        User currentuser = userService.getCurrentUser();

        assertEquals(user, currentuser);
    }

    @Test
    public void shouldGetCurrentUserResponse() {
        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.of(user));
        when(SecurityContextHolder.getContext().getAuthentication().getName())
                .thenReturn("user");

        UserSingleResponse currentUser = userService.getCurrentUserResponse();

        assertEquals(userSingleResponse, currentUser);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenGetCurrentUser() {
        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.empty());
        when(SecurityContextHolder.getContext().getAuthentication().getName())
                .thenReturn("user");

        assertThrows(
                ResponseStatusException.class,
                () -> userService.getCurrentUser()
        );
    }

    @Test
    public void shouldThrowUnauthorizedExceptionWhenGetCurrentUser() {
        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.of(user));
        when(SecurityContextHolder.getContext().getAuthentication())
                .thenReturn(null);

        assertThrows(
                ResponseStatusException.class,
                () -> userService.getCurrentUser()
        );
    }

    @Test
    public void shouldAuthenticateUser() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        userService.authenticate(userSingleRequest, request, response);

        verify(securityContextHolderStrategy, times(1)).setContext(securityContext);
        verify(securityContextRepository, times(1)).saveContext(securityContext, request, response);
    }

    @Test
    public void shouldThrowExceptionWhenAuthenticateUserWithInvalidCredentials() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        userSingleRequest.setPassword("wrong_password");

        assertThrows(ResponseStatusException.class, () -> userService.authenticate(userSingleRequest, request, response));
    }

    @Test
    public void shouldRegisterUser() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());

        userService.register(userSingleRequest);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void shouldThrowConflictExceptionWhenRegisterUserWithExistingUsername() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        assertThrows(ResponseStatusException.class, () -> userService.register(userSingleRequest));
    }

    @Test
    public void shouldChangePassword() {
        UserChangePasswordRequest request = new UserChangePasswordRequest();
        request.setOldPassword("user");
        request.setNewPassword("new_password");

        when(SecurityContextHolder.getContext().getAuthentication().getName())
                .thenReturn("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        userService.changePassword(request);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void shouldThrowBadRequestExceptionWhenChangePasswordWithInvalidOldPassword() {
        UserChangePasswordRequest request = new UserChangePasswordRequest();
        request.setOldPassword("wrong_password");
        request.setNewPassword("new_password");

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        assertThrows(ResponseStatusException.class, () -> userService.changePassword(request));
    }

    @Test
    public void shouldChangeUsername() {
        when(SecurityContextHolder.getContext().getAuthentication().getName())
                .thenReturn("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        StringAsJSON usernameRequest = new StringAsJSON();
        usernameRequest.setValue("new_user");
        userService.changeUsername(usernameRequest.getValue(), request, response);

        verify(userRepository, times(1)).save(any(User.class));
        verify(request, times(2)).getSession(false);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenChangeUsernameOfNonExistingUser() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());

        StringAsJSON usernameRequest = new StringAsJSON();
        usernameRequest.setValue("new_user");

        assertThrows(
                ResponseStatusException.class,
                () -> userService.changeUsername(usernameRequest.getValue(), request, response)
        );
    }
}
