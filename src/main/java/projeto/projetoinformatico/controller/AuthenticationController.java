package projeto.projetoinformatico.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import projeto.projetoinformatico.Exceptions.Exception.InvalidParamsRequestException;
import projeto.projetoinformatico.requests.*;
import projeto.projetoinformatico.service.AuthenticationService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@RequestBody SignUpRequest signUpRequest) {
        // Check if the username already exists
        boolean user = authenticationService.existsByUsername(signUpRequest.getUsername());
        if (user) {
          throw new InvalidParamsRequestException("Username already exists");
        }
        boolean mail = authenticationService.existsByEmail(signUpRequest.getEmail());
        // Check if the email already exists
        if (mail) {
          throw new InvalidParamsRequestException("Email already registered");
        }

        return ResponseEntity.ok(authenticationService.signup(signUpRequest));
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthenticationResponse> signin(@RequestBody SignInRequest signInRequest) {
        AuthenticationResponse response = authenticationService.signin(signInRequest);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationResponse> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok(authenticationService.refreshToken(refreshTokenRequest));
    }

}
