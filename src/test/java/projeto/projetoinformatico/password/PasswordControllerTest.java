package projeto.projetoinformatico.password;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import projeto.projetoinformatico.controllers.PasswordController;
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.exceptions.Exception.InvalidParamsRequestException;
import projeto.projetoinformatico.model.users.User;
import projeto.projetoinformatico.model.users.UserRepository;
import projeto.projetoinformatico.requests.UpdatePasswordRequest;
import projeto.projetoinformatico.service.PasswordService;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class PasswordControllerTest {


    private PasswordService passwordService;
    private UserRepository userRepository;
    private PasswordController passwordController;

    @BeforeEach
    void setUp() {
        passwordService = mock(PasswordService.class);
        userRepository = mock(UserRepository.class);
        passwordController = new PasswordController(passwordService, userRepository);
    }

    @Test
    public void testUpdatePasswordSuccess() {
        // Mocking authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        // Mocking user
        User user = new User();
        user.setUsername("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(user);

        //Mocking UpdatePasswordRequest
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setOldPassword("oldPassword");
        updatePasswordRequest.setNewPassword("newPassword");

        // Mocking password validation
        when(passwordService.validatePassword(user, "oldPassword")).thenReturn(true);

        // Mocking password update
        when(passwordService.updatePassword(user, "newPassword")).thenReturn(new UserDTO());

        // Calling the controller method
        ResponseEntity<UserDTO> responseEntity = passwordController.updatePassword(updatePasswordRequest, authentication);

        // Verifying the response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testUpdatePassword_Error() {
        // Mocking authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        // Mocking user
        User user = new User();
        user.setUsername("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(user);

        //Mocking UpdatePasswordRequest
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setOldPassword("oldPassword");
        updatePasswordRequest.setNewPassword("newPassword");

        // Mocking password validation
        when(passwordService.validatePassword(user, "wrongPassword")).thenReturn(false);

        // Calling the controller method
        try {
            passwordController.updatePassword(updatePasswordRequest, authentication);
        } catch (InvalidParamsRequestException e) {
            // Verifying that the correct exception is thrown
            assertEquals("Old password does not match", e.getMessage());
        }
    }


}
