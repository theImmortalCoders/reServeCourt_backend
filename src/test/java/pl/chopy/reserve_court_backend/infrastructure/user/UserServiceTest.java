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
import pl.chopy.reserve_court_backend.infrastructure.mail.MailTemplateService;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.UserMapper;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.UserMapperImpl;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.UserSingleResponse;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.request.CourtOwnerSingleBecomeRequest;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.request.UserChangePasswordRequest;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.request.UserSingleLoginRequest;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.request.UserSingleRegisterRequest;
import pl.chopy.reserve_court_backend.model.UserRole;
import pl.chopy.reserve_court_backend.model.entity.User;
import pl.chopy.reserve_court_backend.model.entity.repository.UserRepository;
import pl.chopy.reserve_court_backend.util.StringAsJSON;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTest {
    private User user;
    private UserSingleResponse userSingleResponse;
    private UserSingleRegisterRequest userSingleRegisterRequest;
    private UserSingleLoginRequest userSingleLoginRequest;

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
    private MailTemplateService mailTemplateService;
    @Mock
    private SecurityContextHolderStrategy securityContextHolderStrategy;
    @Mock
    private SecurityContextRepository securityContextRepository;

    private final UserRepository userRepository = mock(UserRepository.class);
    @Spy
    private final UserUtil userUtil = new UserUtil(userRepository);

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContextHolderStrategy.createEmptyContext()).thenReturn(securityContext);

        user = new User();
        user.setId(1L);
        user.setEmail("user@mail.com");
        user.setHashedPassword(passwordEncoder.encode("password"));

        userSingleRegisterRequest = new UserSingleRegisterRequest();
        userSingleRegisterRequest.setEmail("user@mail.com");
        userSingleRegisterRequest.setPassword("password");
        userSingleRegisterRequest.setBirthDate(LocalDate.of(2000, 1, 1));

        userSingleLoginRequest = new UserSingleLoginRequest();
        userSingleLoginRequest.setEmail("user@mail.com");
        userSingleLoginRequest.setPassword("password");

        userSingleResponse = new UserSingleResponse();
        userSingleResponse.setId(1L);
        userSingleResponse.setEmail("user@mail.com");
        userSingleResponse.setRole(UserRole.USER);
        userSingleResponse.setActive(true);

        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(SecurityContextHolder.getContext().getAuthentication().getName())
                .thenReturn("user@mail.com");
    }

    @Test
    public void shouldGetCurrentUser() {
        User currentuser = userUtil.getCurrentUser();

        assertEquals(user, currentuser);
    }

    @Test
    public void shouldGetCurrentUserResponse() {
        UserSingleResponse currentUser = userService.getCurrentUserResponse();

        assertEquals(userSingleResponse, currentUser);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenGetCurrentUser() {
        when(userRepository.findByEmail("user@mail.com"))
                .thenReturn(Optional.empty());
        when(SecurityContextHolder.getContext().getAuthentication().getName())
                .thenReturn("user");

        assertThrows(
                ResponseStatusException.class,
                userUtil::getCurrentUser
        );
    }

    @Test
    public void shouldThrowUnauthorizedExceptionWhenGetCurrentUser() {
        when(SecurityContextHolder.getContext().getAuthentication())
                .thenReturn(null);

        assertThrows(
                ResponseStatusException.class,
                userUtil::getCurrentUser
        );
    }

    @Test
    public void shouldAuthenticateUser() {
        userService.authenticate(userSingleLoginRequest, request, response);

        verify(securityContextHolderStrategy, times(1)).setContext(securityContext);
        verify(securityContextRepository, times(1)).saveContext(securityContext, request, response);
    }

    @Test
    public void shouldThrowExceptionWhenAuthenticateUserWithInvalidCredentials() {
        userSingleLoginRequest.setPassword("wrong_password");

        assertThrows(ResponseStatusException.class, () -> userService.authenticate(userSingleLoginRequest, request, response));
    }

    @Test
    public void shouldRegisterUser() {
        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.empty());

        userService.register(userSingleRegisterRequest);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void shouldThrowBadRequestWhenRegisterUser() {
        userSingleRegisterRequest.setBirthDate(LocalDate.of(2012, 1, 1));

        assertThrows(
                ResponseStatusException.class,
                () -> userService.register(userSingleRegisterRequest)
        );
    }

    @Test
    public void shouldThrowConflictExceptionWhenRegisterUserWithExistingUsername() {
        assertThrows(ResponseStatusException.class, () -> userService.register(userSingleRegisterRequest));
    }

    @Test
    public void shouldChangePassword() {
        UserChangePasswordRequest request = new UserChangePasswordRequest();
        request.setOldPassword("password");
        request.setNewPassword("new_password");

        userService.changePassword(request);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void shouldThrowBadRequestExceptionWhenChangePasswordWithInvalidOldPassword() {
        UserChangePasswordRequest request = new UserChangePasswordRequest();
        request.setOldPassword("wrong_password");
        request.setNewPassword("new_password");

        assertThrows(ResponseStatusException.class, () -> userService.changePassword(request));
    }

    @Test
    public void shouldChangeEmail() {
        when(userRepository.save(user)).thenReturn(user);

        StringAsJSON usernameRequest = new StringAsJSON();
        usernameRequest.setValue("new_user@mail.com");
        userService.changeEmail(usernameRequest.getValue(), request, response);

        verify(userRepository, times(1)).save(any(User.class));
        verify(request, times(2)).getSession(false);
    }

    @Test
    public void shouldUpdateRole() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.updateUserRole(1L, UserRole.ADMIN);

        assertEquals(UserRole.ADMIN, user.getRole());
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenChangeUsernameOfNonExistingUser() {
        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.empty());

        StringAsJSON emailRequest = new StringAsJSON();
        emailRequest.setValue("new_user@mail.com");

        assertThrows(
                ResponseStatusException.class,
                () -> userService.changeEmail(emailRequest.getValue(), request, response)
        );
    }

    @Test
    public void shouldDeleteUser() {
        userService.deleteAccount(request, response);

        verify(userRepository, times(1)).delete(any(User.class));
    }

    @Test
    public void shouldBecomeCourtOwner() {
        var ownerRequest = new CourtOwnerSingleBecomeRequest();
        ownerRequest.setAddress("address");
        ownerRequest.setCity("city");
        ownerRequest.setCompanyName("company");

        userService.becomeOwner(ownerRequest, request, response);

        assertEquals(UserRole.OWNER, user.getRole());
    }

    @Test
    public void shouldThrowBadRequestWhenBanUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(
                ResponseStatusException.class,
                () -> userService.banUser(1L)
        );
    }

    @Test
    public void shouldBanUser() {
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@mail.com");

        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        userService.banUser(2L);

        assertFalse(user2.isActive());
    }
}
