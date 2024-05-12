package projeto.projetoinformatico.authenticationTest;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import projeto.projetoinformatico.controllers.AuthenticationController;
import projeto.projetoinformatico.controllers.UserController;
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.model.users.User;
import projeto.projetoinformatico.model.users.UserRepository;
import projeto.projetoinformatico.requests.RefreshTokenRequest;
import projeto.projetoinformatico.requests.SignInRequest;
import projeto.projetoinformatico.requests.SignUpRequest;
import projeto.projetoinformatico.responses.AuthenticationResponse;
import projeto.projetoinformatico.responses.JwtAuthenticationResponse;
import projeto.projetoinformatico.service.AuthenticationService;
import projeto.projetoinformatico.service.JWT.JWTServiceImpl;
import projeto.projetoinformatico.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class AuthenticationControllerTest {

    @Test
    public void testSignUp_Success() {
        // Mock dependencies
        AuthenticationService authService = Mockito.mock(AuthenticationService.class);
        AuthenticationController authController = new AuthenticationController(authService);

        // Create a mock sign-up request
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("testUser");
        signUpRequest.setPassword("password123");
        signUpRequest.setEmail("test@example.com");

        // Mock behavior of authService.signup
        UserDTO mockUserDTO = new UserDTO();
        mockUserDTO.setUsername("testUser");
        mockUserDTO.setEmail("test@example.com");
        mockUserDTO.setRole("USER");
        when(authService.signup(signUpRequest)).thenReturn(mockUserDTO);


        // Call the endpoint
        ResponseEntity<UserDTO> response = authController.signup(signUpRequest);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockUserDTO, response.getBody());
    }

    @Test//Falha
    public void testSignIn_Success() {
        // Mock dependencies
        AuthenticationService authService = Mockito.mock(AuthenticationService.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        AuthenticationController authController = new AuthenticationController(authService);

        // Create a mock sign-up request
        // Create a mock sign-in request
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setUsername("Admin");
        signInRequest.setPassword("admin");

        // Mock behavior of authService.signin
        AuthenticationResponse mockResponse = new AuthenticationResponse();

        // Call the endpoint
        ResponseEntity<AuthenticationResponse> response = authController.signin(signInRequest);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    /*
    @Test
    public void testRefreshToken_Success() {
        // Mock dependencies
        AuthenticationService authService = Mockito.mock(AuthenticationService.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        AuthenticationController authController = new AuthenticationController(authService);

        // Mock request
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setToken("valid_jwt_token");

        // Mock behavior of JWTServiceImpl methods
        when(JWTServiceImpl.extractUsername(refreshTokenRequest.getToken())).thenReturn("testUser");
        when(JWTServiceImpl.isTokenValid(refreshTokenRequest.getToken(), null)).thenReturn(true);
        when(JWTServiceImpl.generateToken(Mockito.any(User.class))).thenReturn("new_jwt_token");

        // Mock user retrieval
        User mockUser = new User();
        mockUser.setUsername("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(mockUser);

        // Mock behavior of authService.refreshToken
        JwtAuthenticationResponse mockResponse = new JwtAuthenticationResponse();
        mockResponse.setToken("new_jwt_token");
        mockResponse.setRefreshToken(refreshTokenRequest.getToken());
        when(authService.refreshToken(refreshTokenRequest)).thenReturn(mockResponse);

        // Call the endpoint
        ResponseEntity<JwtAuthenticationResponse> response = authController.refresh(refreshTokenRequest);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

     */

}
