package projeto.projetoinformatico.admin;

import com.github.jsonldjava.shaded.com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import projeto.projetoinformatico.controllers.AdminController;
import projeto.projetoinformatico.exceptions.Exception.InvalidRequestException;
import projeto.projetoinformatico.service.LayerService;
import projeto.projetoinformatico.service.UserService;
import projeto.projetoinformatico.utils.Validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AdminControllerTest {

    private UserService userService;

    @BeforeEach
    public void setUp(){
        userService = mock(UserService.class);

    }
    @Test
    public void testSayHello_Success() {
        // Mock dependencies
        AdminController adminController = new AdminController(userService);

        // Call the endpoint
        ResponseEntity<String> response = adminController.sayHello();

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Welcome Admin", response.getBody());
    }

    @Test
    public void testblockUser_Success() {
        // Mock dependencies
        AdminController adminController = new AdminController(userService);

        // Set up mock behavior
        doNothing().when(userService).blockUser(anyLong());

        // Call the endpoint
        ResponseEntity<Void> response = adminController.blockUser(1L);

        // Assert the response
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

    }

    @Test
    public void testBlockUser_UserNotFound(){
        AdminController adminController = new AdminController(userService);

        // Mock the UserService blockUser method to throw an exception for user not found
        doThrow(new RuntimeException("User not found")).when(userService).blockUser(anyLong());

        // Call the controller method and handle the exception
        try {
            adminController.blockUser(1L);
        } catch (RuntimeException e) {
            // Assert the exception
            assertEquals("User not found", e.getMessage());
        }
    }

    @Test
    public void testBlockUser_BlockAdminUser() {
        AdminController adminController = new AdminController(userService);

        // Mock the UserService blockUser method to throw an exception for blocking an admin user
        doThrow(new RuntimeException("Cannot block an admin user.")).when(userService).blockUser(anyLong());

        // Call the controller method and handle the exception
        try {
            adminController.blockUser(1L);
        } catch (RuntimeException e) {
            // Assert the exception
            assertEquals("Cannot block an admin user.", e.getMessage());
        }
    }

    @Test
    public void testUnblockUser_Success() {
        AdminController adminController = new AdminController(userService);

        // Mock the UserService unblockUser method
        doNothing().when(userService).unblockUser(anyLong());

        // Call the controller method
        ResponseEntity<Void> response = adminController.unlockUser(1L);

        // Assert the response
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testUnblockUser_UserNotFound(){
        AdminController adminController = new AdminController(userService);

        // Mock the UserService unblockUser method to throw an exception for user not found
        doThrow(new RuntimeException("User not found")).when(userService).unblockUser(anyLong());

        // Call the controller method and handle the exception
        try {
            adminController.unlockUser(1L);
        } catch (RuntimeException e) {
            // Assert the exception
            assertEquals("User not found", e.getMessage());
        }
    }

    @Test
    public void testdeleteUserById_Success() {
        AdminController adminController = new AdminController(userService);

        // Mock the UserService deleteUser method
        doNothing().when(userService).deleteUser(anyLong());

        // Call the controller method
        ResponseEntity<Void> response = adminController.deleteUserById(1L);

        // Assert the response
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testdeleteUserById_UserNotFound(){
        AdminController adminController = new AdminController(userService);

        // Mock the UserService deleteUser method to throw an exception for user not found
        doThrow(new RuntimeException("User not found")).when(userService).deleteUser(anyLong());

        // Call the controller method and handle the exception
        try {
            adminController.deleteUserById(1L);
        } catch (RuntimeException e) {
            // Assert the exception
            assertEquals("User not found", e.getMessage());
        }
    }

    @Test
    public void testDeleteUserById_AdminDeletionNotAllowed() {
        AdminController adminController = new AdminController(userService);

        // Mock the UserService deleteUser method to throw InvalidRequestException for deleting an admin user
        doThrow(new InvalidRequestException("Admin users cannot delete other admin users.")).when(userService).deleteUser(anyLong());

        // Call the controller method and handle the exception
        try {
            adminController.deleteUserById(1L);
        } catch (InvalidRequestException e) {
            // Assert the exception
            assertEquals("Admin users cannot delete other admin users.", e.getMessage());
        }
    }
}
