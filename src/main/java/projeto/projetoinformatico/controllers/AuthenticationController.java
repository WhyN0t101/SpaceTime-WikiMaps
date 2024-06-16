package projeto.projetoinformatico.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.exceptions.Exception.*;
import projeto.projetoinformatico.requests.*;
import projeto.projetoinformatico.responses.AuthenticationResponse;
import projeto.projetoinformatico.responses.JwtAuthenticationResponse;
import projeto.projetoinformatico.service.AuthenticationService;
import projeto.projetoinformatico.service.UserService;


/**
 * Controller class for handling authentication operations.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    /**
     * Endpoint for user registration (signup).
     *
     * @param signUpRequest The request object containing user signup details.
     * @return ResponseEntity with the registered user DTO upon successful signup.
     */
    @Operation(summary = "User registration (signup)", description = "Endpoint for user registration (signup).")
    @PostMapping("/signup")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid signup request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserDTO> signup(
            @Parameter(description = "User signup details", required = true)
            @Valid @RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.ok(authenticationService.signup(signUpRequest));
    }

    /**
     * Endpoint for user authentication (signin).
     *
     * @param signInRequest The request object containing user signin credentials.
     * @return ResponseEntity with authentication response upon successful signin.
     * @throws AccountBlockedException If the user account is locked or blocked.
     */
    @Operation(summary = "User authentication (signin)", description = "Endpoint for user authentication (signin).")
    @PostMapping("/signin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully authenticated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "User account is locked"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AuthenticationResponse> signin(
            @Parameter(description = "User signin credentials", required = true)
            @Valid @RequestBody SignInRequest signInRequest) {
        UserDTO user = userService.getUserByUsername(signInRequest.getUsername());
        if (user.isBlocked()) {
            throw new AccountBlockedException("User account is locked");
        }
        AuthenticationResponse response = authenticationService.signin(signInRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to refresh JWT authentication token.
     *
     * @param refreshTokenRequest The request object containing refresh token.
     * @return ResponseEntity with refreshed JWT authentication response.
     * @throws JwtExpiredException If the JWT token has expired.
     */
    @Operation(summary = "Refresh JWT token", description = "Endpoint to refresh JWT authentication token.")
    @PostMapping("/refresh")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "JWT token successfully refreshed"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    //@PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<JwtAuthenticationResponse> refresh(
            @Parameter(description = "Refresh token details", required = true)
            @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) throws JwtExpiredException {
        return ResponseEntity.ok(authenticationService.refreshToken(refreshTokenRequest));
    }

}