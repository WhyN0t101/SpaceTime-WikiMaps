package projeto.projetoinformatico.users;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import projeto.projetoinformatico.controllers.UserController;
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.exceptions.Exception.NotFoundException;
import projeto.projetoinformatico.model.layers.Layer;
import projeto.projetoinformatico.model.users.Role;
import projeto.projetoinformatico.service.UserService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    @Test
    public void testGetUserByUsername_Success() {
        // Mock dependencies
        UserService userService = Mockito.mock(UserService.class);
        UserController userController = new UserController(userService);
        UserDTO userDTO = new UserDTO();

        // Set up mock behavior
        when(userService.getUserByUsername("Admin")).thenReturn(userDTO);

        // Call the endpoint
        ResponseEntity<UserDTO> response = userController.getUserByUsername("Admin");

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
    }

    @Test
    public void testGetUserByUsername_UserNotFound() {
        // Mock dependencies
        UserService userService = Mockito.mock(UserService.class);
        UserController userController = new UserController(userService);

        // Set up mock behavior
        when(userService.getUserByUsername("nonexistentUser")).thenThrow(new NotFoundException("User not found"));

        // Call the endpoint and assert that it throws UserNotFoundException
        assertThrows(NotFoundException.class, () -> userController.getUserByUsername("nonexistentUser"));
    }

    @Test
    public void testGetAllUsers_Success() {
        // Mock dependencies
        UserService userService = Mockito.mock(UserService.class);
        UserController userController = new UserController(userService);
        List<UserDTO> userDTOs = Arrays.asList(new UserDTO(), new UserDTO());

        // Set up mock behavior
        when(userService.getAllUsers()).thenReturn(userDTOs);

        // Call the endpoint
        ResponseEntity<List<UserDTO>> response = userController.getAllUsers(null, null);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTOs, response.getBody());
    }

    @Test
    public void testGetAllUsersByRole_Success() {
        // Mock dependencies
        UserService userService = Mockito.mock(UserService.class);
        UserController userController = new UserController(userService);
        List<UserDTO> userDTOS = Arrays.asList(new UserDTO(), new UserDTO());

        // Set up mock behavior
        when(userService.getAllUsersByRole(Role.USER.toString())).thenReturn(userDTOS);

        // Call the endpoint
        ResponseEntity<List<UserDTO>> response = userController.getAllUsersByRole(Role.USER.toString());

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTOS, response.getBody());
    }

    @Test
    public void testGetUserById_Success() {
        // Mock dependencies
        UserService userService = Mockito.mock(UserService.class);
        UserController userController = new UserController(userService);
        UserDTO userDTO = new UserDTO();

        // Set up mock behavior
        when(userService.getUserById(1L)).thenReturn(userDTO);

        // Call the endpoint
        ResponseEntity<UserDTO> response = userController.getUserById(1L);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
    }

    @Test//Falha
    public void testGetUserLayers_Success() {
        // Mock dependencies
        UserService userService = Mockito.mock(UserService.class);
        UserController userController = new UserController(userService);

        //MockLayers
        List<Layer> layers = Arrays.asList(new Layer(), new Layer());
        String username = "Admin";
        Long userId = 1L;


        // Set up mock behavior
        when(userService.getUserLayers(username)).thenReturn(layers); // Replace "Admin" with the expected username
        // Alternatively, you can use ArgumentMatchers.any() to match any string argument
        // when(userService.getUserLayers(ArgumentMatchers.any())).thenReturn(layers);

        // Call the endpoint
        ResponseEntity<List<Layer>> response = userController.getUserLayers(userId);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(layers, response.getBody());
    }


    @Test
    public void testGetAuthenticatedUser_Success() {
        // Mock dependencies
        UserService userService = Mockito.mock(UserService.class);
        UserController userController = new UserController(userService);
        UserDTO userResponse = new UserDTO();
        Authentication authentication = Mockito.mock(Authentication.class);

        // Set up mock behavior
        when(authentication.getName()).thenReturn("Admin");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userService.getUserByUsername("Admin")).thenReturn(userResponse);

        // Call the endpoint
        ResponseEntity<UserDTO> response = userController.getAuthenticatedUser();

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponse, response.getBody());
    }

    @Test
    public void testSayHello_Success() {
        // Mock dependencies
        UserService userService = Mockito.mock(UserService.class);
        UserController userController = new UserController(userService);

        // Call the endpoint
        ResponseEntity<String> response = userController.sayHello();

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Welcome User", response.getBody());
    }

}