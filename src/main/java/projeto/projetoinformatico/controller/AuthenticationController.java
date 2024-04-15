package projeto.projetoinformatico.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import projeto.projetoinformatico.requests.JwtAuthenticationResponse;
import projeto.projetoinformatico.requests.RefreshTokenRequest;
import projeto.projetoinformatico.requests.SignInRequest;
import projeto.projetoinformatico.requests.SignUpRequest;
import projeto.projetoinformatico.service.AuthenticationService;
import projeto.projetoinformatico.model.users.User;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.ok(authenticationService.signup(signUpRequest))   ;
    }


    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponse> signup(@RequestBody SignInRequest signInRequest) {
        return ResponseEntity.ok(authenticationService.signin(signInRequest))   ;
    }
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationResponse> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok(authenticationService.refreshToken(refreshTokenRequest));
    }

}
