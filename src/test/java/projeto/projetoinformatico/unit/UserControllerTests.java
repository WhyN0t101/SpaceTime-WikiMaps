package projeto.projetoinformatico.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import projeto.projetoinformatico.controller.UserController;
import projeto.projetoinformatico.requests.SignUpRequest;
import projeto.projetoinformatico.responses.UserResponse;
import projeto.projetoinformatico.service.AuthenticationService;

public class UserControllerTests {

    @Test
    public void testSignup_Success() {
        // Mocking dependencies
        AuthenticationService authenticationService = mock(AuthenticationService.class);
        SignUpRequest signUpRequest = new SignUpRequest();
        // Set up any necessary data in the signUpRequest

        // Mock the behavior of the AuthenticationService
        when(authenticationService.signup(signUpRequest)).thenReturn(new UserResponse());

        // Create UserController instance with mocked dependencies
        UserController userController = new UserController(authenticationService);

        // Call the signup endpoint
        ResponseEntity<UserResponse> responseEntity = userController.signup(signUpRequest);

        // Assert the response status
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        // Add more assertions as needed
    }

    @Test
    public void testSignup_InvalidEmail() {
        // Mocking dependencies
        AuthenticationService authenticationService = mock(AuthenticationService.class);
        SignUpRequest signUpRequest = new SignUpRequest();
        // Set up signUpRequest with invalid email

        // Create UserController instance with mocked dependencies
        UserController userController = new UserController(authenticationService);

        // Call the signup endpoint and assert that it throws InvalidEmailFormatException
        assertThrows(InvalidEmailFormatException.class, () -> {
            userController.signup(signUpRequest);
        });
    }

    @Test
    public void testSignup_InvalidParams() {
        // Mocking dependencies
        AuthenticationService authenticationService = mock(AuthenticationService.class);
        SignUpRequest signUpRequest = new SignUpRequest();
        // Set up signUpRequest with invalid parameters

        // Mock the behavior of the AuthenticationService
        doThrow(InvalidParamsRequestException.class).when(authenticationService).signup(signUpRequest);

        // Create UserController instance with mocked dependencies
        UserController userController = new UserController(authenticationService);

        // Call the signup endpoint and assert that it throws InvalidParamsRequestException
        assertThrows(InvalidParamsRequestException.class, () -> {
            userController.signup(signUpRequest);
        });
    }
}
