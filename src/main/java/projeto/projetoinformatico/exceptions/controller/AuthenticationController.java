package projeto.projetoinformatico.exceptions.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import projeto.projetoinformatico.exceptions.Exception.InvalidParamsRequestException;
import projeto.projetoinformatico.exceptions.Exception.JwtExpiredException;
import projeto.projetoinformatico.requests.*;
import projeto.projetoinformatico.responses.AuthenticationResponse;
import projeto.projetoinformatico.responses.JwtAuthenticationResponse;
import projeto.projetoinformatico.responses.UserResponse;
import projeto.projetoinformatico.service.AuthenticationService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody SignUpRequest signUpRequest) {
        // Check if the username already exists
        return ResponseEntity.ok(authenticationService.signup(signUpRequest));
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthenticationResponse> signin(@Valid @RequestBody SignInRequest signInRequest) {
        AuthenticationResponse response = authenticationService.signin(signInRequest);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationResponse> refresh(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) throws JwtExpiredException {
        return ResponseEntity.ok(authenticationService.refreshToken(refreshTokenRequest));
    }

}
