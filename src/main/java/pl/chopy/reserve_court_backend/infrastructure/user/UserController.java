package pl.chopy.reserve_court_backend.infrastructure.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.UserSingleResponse;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.request.UserChangePasswordRequest;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.request.UserSingleRequest;
import pl.chopy.reserve_court_backend.model.StringAsJSON;

@RestController
@Tag(name = "User", description = "user authentication operations")
@RequestMapping("/api")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Authentication using username and password")
    @ApiResponse(responseCode = "200", description = "Successfully authenticated")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public void authenticate(@RequestBody @Valid UserSingleRequest userRequest, HttpServletRequest request, HttpServletResponse response) {
        userService.authenticate(userRequest, request, response);
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Registration of new user")
    @ApiResponse(responseCode = "200", description = "Successfully registered")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "409", description = "User already exists")
    public void register(@RequestBody @Valid UserSingleRequest request) {
        userService.register(request);
    }

    @PatchMapping("/change-password")
    @Operation(summary = "Change password (Auth)", description = "Change password of current user")
    @ApiResponse(responseCode = "200", description = "Successfully changed password")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PreAuthorize("isAuthenticated()")
    public void changePassword(@RequestBody @Valid UserChangePasswordRequest request) {
        userService.changePassword(request);
    }

    @PatchMapping("/change-username")
    @Operation(summary = "Change username (Auth)", description = "Change username of current user")
    @ApiResponse(responseCode = "200", description = "Successfully changed password")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "User not found")
    @PreAuthorize("isAuthenticated()")
    public void changeUsername(@RequestBody StringAsJSON username, HttpServletRequest request, HttpServletResponse response) {
        userService.changeUsername(username.getValue(), request, response);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user (Auth)", description = "Get current user details (check if authenticated)")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserSingleResponse> me() {
        return ResponseEntity.ok(userService.getCurrentUserResponse());
    }

    @GetMapping("/logout")
    @Operation(summary = "Logout user (Auth)", description = "Logout current user")
    @ApiResponse(responseCode = "200", description = "Successfully logged out")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PreAuthorize("isAuthenticated()")
    public void logout() {
    }
}
