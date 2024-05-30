package projeto.projetoinformatico.authenticationTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import projeto.projetoinformatico.controllers.AuthenticationController;
import projeto.projetoinformatico.controllers.UserController;
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.exceptions.Exception.AccountBlockedException;
import projeto.projetoinformatico.exceptions.Exception.InvalidPasswordException;
import projeto.projetoinformatico.exceptions.Exception.JwtExpiredException;
import projeto.projetoinformatico.exceptions.Exception.NotFoundException;
import projeto.projetoinformatico.model.layers.LayersRepository;
import projeto.projetoinformatico.model.roleUpgrade.RoleUpgradeRepository;
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
import projeto.projetoinformatico.utils.ModelMapperUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class AuthenticationControllerTest {


    private UserRepository userRepository;
    private UserService userService;
    private AuthenticationService authenticationService;
    private AuthenticationManager authenticationManager;
    private AuthenticationController authenticationController;
    private LayersRepository layersRepository;
    private RoleUpgradeRepository roleUpgradeRepository;
    private ModelMapperUtils mapperUtils;

    @BeforeEach
    public void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        authenticationService = Mockito.mock(AuthenticationService.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        layersRepository = Mockito.mock(LayersRepository.class);
        roleUpgradeRepository = Mockito.mock(RoleUpgradeRepository.class);
        mapperUtils = Mockito.mock(ModelMapperUtils.class);
        userService = new UserService(userRepository, layersRepository, mapperUtils, roleUpgradeRepository);
        authenticationController = new AuthenticationController(authenticationService, userService);
    }

    @Test
    public void testSignUp_Success() {

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
        when(authenticationService.signup(signUpRequest)).thenReturn(mockUserDTO);


        // Call the endpoint
        ResponseEntity<UserDTO> response = authenticationController.signup(signUpRequest);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockUserDTO, response.getBody());
    }


    @Test
    public void testSignin_UserNotFound() {
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setUsername("nonexistentuser");
        signInRequest.setPassword("password");

        when(userRepository.findByUsername("nonexistentuser")).thenReturn(null);

        try {
            authenticationController.signin(signInRequest);
        } catch (NotFoundException e) {
            assertEquals("User not found with username: nonexistentuser", e.getMessage());
        }
    }
/*
    @Test
    public void testSignin_AccountBlocked() {
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setUsername("lockeduser");
        signInRequest.setPassword("password");

        User user = new User();
        user.setUsername("lockeduser");
        user.setPassword("hashedpassword");
        user.setAccountNonLocked(false);

        when(userRepository.findByUsername("lockeduser")).thenReturn(user);

        try {
            authenticationController.signin(signInRequest);
        } catch (AccountBlockedException e) {
            assertEquals("User account is locked", e.getMessage());
        }
    }
*/
    /*@Test
    public void testSignin_InvalidPassword() {
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setUsername("testuser");
        signInRequest.setPassword("wrongpassword");

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("hashedpassword");
        user.setAccountNonLocked(true);

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        doThrow(new InvalidPasswordException("Invalid Password")).when(authenticationManager).authenticate
                (new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword()));

        try {
            authenticationController.signin(signInRequest);
        } catch (InvalidPasswordException e) {
            assertEquals("Invalid Password", e.getMessage());
        }
    }*/

    @Test
    public void testRefresh_Success() throws JwtExpiredException {
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setToken("valid-refresh-token");

        JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();
        jwtAuthenticationResponse.setToken("new-jwt-token");
        jwtAuthenticationResponse.setRefreshToken("valid-refresh-token");

        when(authenticationService.refreshToken(refreshTokenRequest)).thenReturn(jwtAuthenticationResponse);

        ResponseEntity<JwtAuthenticationResponse> response = authenticationController.refresh(refreshTokenRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(jwtAuthenticationResponse, response.getBody());
    }

    @Test
    public void testRefresh_UserNotFound() throws JwtExpiredException {
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setToken("valid-refresh-token");

        when(authenticationService.refreshToken(refreshTokenRequest)).thenThrow(new NotFoundException("User not found"));

        try {
            authenticationController.refresh(refreshTokenRequest);
        } catch (NotFoundException e) {
            assertEquals("User not found", e.getMessage());
        }
    }

    @Test
    public void testRefresh_InvalidToken() throws JwtExpiredException {
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setToken("invalid-refresh-token");

        when(authenticationService.refreshToken(refreshTokenRequest)).thenThrow(new JwtExpiredException("Token is expired"));

        try {
            authenticationController.refresh(refreshTokenRequest);
        } catch (JwtExpiredException e) {
            assertEquals("Token is expired", e.getMessage());
        }
    }

}


