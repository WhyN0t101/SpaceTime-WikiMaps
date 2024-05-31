package projeto.projetoinformatico.authenticationTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.exceptions.Exception.InvalidParamsRequestException;
import projeto.projetoinformatico.exceptions.Exception.InvalidPasswordException;
import projeto.projetoinformatico.exceptions.Exception.NotFoundException;
import projeto.projetoinformatico.model.roleUpgrade.RoleUpgradeRepository;
import projeto.projetoinformatico.model.users.Role;
import projeto.projetoinformatico.model.users.User;
import projeto.projetoinformatico.model.users.UserRepository;
import projeto.projetoinformatico.requests.RefreshTokenRequest;
import projeto.projetoinformatico.requests.SignInRequest;
import projeto.projetoinformatico.requests.SignUpRequest;
import projeto.projetoinformatico.responses.AuthenticationResponse;
import projeto.projetoinformatico.responses.JwtAuthenticationResponse;
import projeto.projetoinformatico.service.AuthenticationService;
import projeto.projetoinformatico.service.JWT.JWTServiceImpl;
import projeto.projetoinformatico.utils.ModelMapperUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
//@PowerMockRunnerDelegate(MockitoExtension.class)
@PrepareForTest({JWTServiceImpl.class})
public class AuthenticationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RoleUpgradeRepository roleUpgradeRepository;

    @Mock
    private JWTServiceImpl jwtService;

    @Mock
    private ModelMapperUtils modelMapperUtils;

    @Mock
    private UserDetails userDetails;


    @InjectMocks
    private AuthenticationService authenticationService;




    @Test
    public void testSignup_Success() {
        // Create a mock sign-up request
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("testUser");
        signUpRequest.setPassword("password123");
        signUpRequest.setEmail("test@example.com");

        // Mock behavior of userRepository
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        // Mock behavior of passwordEncoder
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Mock behavior of modelMapperUtils
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(signUpRequest.getUsername());
        userDTO.setEmail(signUpRequest.getEmail());
        userDTO.setRole(Role.USER.toString());
        when(modelMapperUtils.userToDTO(any(User.class), eq(UserDTO.class))).thenReturn(userDTO);

        // Call the method under test
        UserDTO resultDTO  = authenticationService.signup(signUpRequest);

        // Assert the result
        assertNotNull(resultDTO );
        assertEquals("testUser", resultDTO .getUsername());
        assertEquals("test@example.com", resultDTO .getEmail());
        assertEquals(Role.USER.toString(), resultDTO .getRole());

        // Optionally, you can verify interactions if needed
        verify(userRepository, times(1)).existsByUsername("testUser");
        verify(userRepository, times(1)).existsByEmail("test@example.com");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(modelMapperUtils, times(1)).userToDTO(any(User.class), eq(UserDTO.class));
    }

    @Test
    public void testSignup_ExistingUsername() {
        // Create a mock sign-up request
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("existingUser");
        signUpRequest.setPassword("password123");
        signUpRequest.setEmail("test@example.com");

        // Mock behavior of userRepository
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // Call the method under test and assert exception
        assertThrows(InvalidParamsRequestException.class, () -> authenticationService.signup(signUpRequest));

        // Verify interaction with userRepository
        verify(userRepository, times(1)).existsByUsername("existingUser");
        verify(userRepository, never()).existsByEmail(anyString());
    }

    @Test
    public void testSignup_ExistingEmail() {
        // Create a mock sign-up request
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("newUser");
        signUpRequest.setPassword("password123");
        signUpRequest.setEmail("existing@example.com");

        // Mock behavior of userRepository
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Call the method under test and assert exception
        assertThrows(InvalidParamsRequestException.class, () -> authenticationService.signup(signUpRequest));

        // Verify interaction with userRepository
        verify(userRepository, times(1)).existsByUsername("newUser");
        verify(userRepository, times(1)).existsByEmail("existing@example.com");
    }

    /*
    @Test
    public void testSignin_Success() {
        // Create a mock sign-in request
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setUsername("existingUser");
        signInRequest.setPassword("password123");

        // Create a mock user
        User user = new User();
        user.setId(1L);
        user.setUsername("existingUser");
        user.setEmail("existing@example.com");
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode("password123"));

        // Mock behavior of userRepository
        when(userRepository.findByUsername(anyString())).thenReturn(user);

        // Mock behavior of passwordEncoder
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // Mock behavior of authenticationManager
        Authentication authentication = mock(Authentication.class);
        userDetails = new org.springframework.security.core.userdetails.User(
                "existingUser", "password123", Collections.emptyList());
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // Mock JWT tokens
        String jwtToken = "mockJWTToken";
        String refreshToken = "mockRefreshToken";
        when(jwtService.generateToken(any())).thenReturn(jwtToken);
        when(jwtService.generateRefreshToken(any(), any())).thenReturn(refreshToken);

        // Call the method under test
        AuthenticationResponse response = authenticationService.signin(signInRequest);

        // Assert the result
        assertNotNull(response);
        assertEquals(jwtToken, response.getAccessToken());
        assertEquals(refreshToken, response.getRefreshToken());
        assertNotNull(response.getUser());
        assertEquals("existingUser", response.getUser().getUsername());
        assertEquals("existing@example.com", response.getUser().getEmail());
        assertEquals(Role.USER.toString(), response.getUser().getRole());

        // Verify interactions
        verify(userRepository, times(1)).findByUsername("existingUser");
        verify(passwordEncoder, times(1)).matches("password123", user.getPassword());
        verify(authenticationManager, times(1)).authenticate(any(Authentication.class));
        verify(jwtService, times(1)).generateToken(user);
        verify(jwtService, times(1)).generateRefreshToken(any(), eq(user));
    }
*/

    @Test
    void testSigninUserNotFound() {
        // Arrange
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setUsername("nonExistingUser");
        signInRequest.setPassword("password");

        when(userRepository.findByUsername("nonExistingUser")).thenReturn(null);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> authenticationService.signin(signInRequest));
        verify(userRepository, times(1)).findByUsername("nonExistingUser");
    }

    @Test
    void testSigninPasswordMismatch() {
        // Arrange
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setUsername("testUser");
        signInRequest.setPassword("wrongPassword");

        User user = new User();
        user.setUsername("testUser");
        user.setPassword("hashedPassword");

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidPasswordException.class, () -> authenticationService.signin(signInRequest));
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(passwordEncoder, times(1)).matches("wrongPassword", "hashedPassword");
    }

    @Test
    void testSigninUnexpectedException() {
        // Arrange
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setUsername("testUser");
        signInRequest.setPassword("testPassword");

        when(userRepository.findByUsername("testUser")).thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authenticationService.signin(signInRequest));
        verify(userRepository, times(1)).findByUsername("testUser");
    }

/*
    @Test
    void testRefreshTokenSuccess() {
        // Arrange
        String token = "valid.token.string";
        String username = "testUser";

        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setToken(token);

        User user = new User();
        user.setUsername(username);

        when(JWTServiceImpl.extractUsername(token)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(user);
        when(JWTServiceImpl.isTokenValid(token, user)).thenReturn(true);
        when(JWTServiceImpl.generateToken(user)).thenReturn("newJwtToken");

        // Act
        JwtAuthenticationResponse response = authenticationService.refreshToken(refreshTokenRequest);

        // Assert
        assertNotNull(response);
        assertEquals("newJwtToken", response.getToken());
        assertEquals(token, response.getRefreshToken());
    }

    @Test
    void testRefreshTokenUserNotFound() {
        // Arrange
        String token = "validToken";
        String username = "nonExistingUser";

        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setToken(token);

        PowerMockito.mockStatic(JWTServiceImpl.class);
        when(JWTServiceImpl.extractUsername(token)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(null);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> authenticationService.refreshToken(refreshTokenRequest));
    }

    @Test
    void testRefreshTokenInvalidToken() {
        // Arrange
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VybmFtZSIsImlhdCI6MTYyMzI0NjQ5MCwiZXhwIjoxNjIzMjUwMjkwfQ.8di9KSH1P4s-0A4xXVO6l1FLLpnE83LRhH6Y1Y6ck6U";
        String username = "testUser";

        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setToken(token);

        User user = new User();
        user.setUsername(username);

        when(JWTServiceImpl.extractUsername(token)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(user);
        when(JWTServiceImpl.isTokenValid(token, user)).thenReturn(true); // Mock to return true

        // Act
        JwtAuthenticationResponse response = authenticationService.refreshToken(refreshTokenRequest);

        // Assert
        assertNull(response); // Update assertion as needed
    }
*/

}
