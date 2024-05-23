package projeto.projetoinformatico.controllers;

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
import projeto.projetoinformatico.model.users.UserRepository;
import projeto.projetoinformatico.requests.*;
import projeto.projetoinformatico.responses.AuthenticationResponse;
import projeto.projetoinformatico.responses.JwtAuthenticationResponse;
import projeto.projetoinformatico.service.AuthenticationService;
import projeto.projetoinformatico.service.UserService;

import javax.security.auth.login.AccountLockedException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signup(@Valid @RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.ok(authenticationService.signup(signUpRequest));
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthenticationResponse> signin(@Valid @RequestBody SignInRequest signInRequest) {
        UserDTO user = userService.getUserByUsername(signInRequest.getUsername());
        if (user.isBlocked()) {
            throw new AccountBlockedException("User account is locked");
        }
        AuthenticationResponse response = authenticationService.signin(signInRequest);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationResponse> refresh(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) throws JwtExpiredException {
        return ResponseEntity.ok(authenticationService.refreshToken(refreshTokenRequest));
    }

}
