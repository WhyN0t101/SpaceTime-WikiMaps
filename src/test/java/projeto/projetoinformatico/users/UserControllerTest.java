package projeto.projetoinformatico.users;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import projeto.projetoinformatico.controllers.UserController;
import projeto.projetoinformatico.dtos.LayerDTO;
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.exceptions.Exception.NotFoundException;
import projeto.projetoinformatico.model.layers.Layer;
import projeto.projetoinformatico.model.users.Role;
import projeto.projetoinformatico.service.UserService;

import java.util.Arrays;
import java.util.Collections;
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
    public void testGetAllUsers_EmptyList() {
        // Mock dependencies
        UserService userService = Mockito.mock(UserService.class);
        UserController userController = new UserController(userService);

        // Mock behavior of userService.getAllUsers to return an empty list
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        // Call the endpoint
        ResponseEntity<List<UserDTO>> response = userController.getAllUsers(null, null);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
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
    public void testGetAllUsersByRole_Error() {
        // Mock dependencies
        UserService userService = Mockito.mock(UserService.class);
        UserController userController = new UserController(userService);

        // Mock behavior of userService.getAllUsersByRole to throw NotFoundException for both scenarios
        when(userService.getAllUsersByRole("nonExistent")).thenThrow(new NotFoundException("Role not found: nonExistent"));
        when(userService.getAllUsersByRole("emptyRole")).thenThrow(new NotFoundException("No users found with role: emptyRole"));

        // Call the endpoint and assert that it throws NotFoundException for both scenarios
        assertThrows(NotFoundException.class, () -> {
            userController.getAllUsersByRole("nonExistent");
        });

        assertThrows(NotFoundException.class, () -> {
            userController.getAllUsersByRole("emptyRole");
        });
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

    @Test
    public void testGetUserById_UserNotFound() {
        // Mock dependencies
        UserService userService = Mockito.mock(UserService.class);
        UserController userController = new UserController(userService);

        // Mock behavior of userService.getUserById to throw NotFoundException
        when(userService.getUserById(1000L)).thenThrow(new NotFoundException("User not found with id: 1000"));

        // Call the endpoint and assert that it throws NotFoundException
        assertThrows(NotFoundException.class, () -> {
            userController.getUserById(1000L);
        });
    }

    @Test
    public void testGetUserLayers_Success() {
        // Mock dependencies
        UserService userService = Mockito.mock(UserService.class);
        UserController userController = new UserController(userService);

        //MockLayers
        List<LayerDTO> layers = Arrays.asList(new LayerDTO(), new LayerDTO());
        String username = "Admin";
        Long userId = 1L;


        // Set up mock behavior
        when(userService.getUserLayers(1L)).thenReturn(layers); // Replace "Admin" with the expected username
        // Alternatively, you can use ArgumentMatchers.any() to match any string argument
        // when(userService.getUserLayers(ArgumentMatchers.any())).thenReturn(layers);

        // Call the endpoint
        ResponseEntity<List<LayerDTO>> response = userController.getUserLayers(userId);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(layers, response.getBody());
    }

    @Test
    public void testGetUserLayers_UserLayersNotFound() {
        // Mock dependencies
        UserService userService = Mockito.mock(UserService.class);
        UserController userController = new UserController(userService);

        // Mock behavior of userService.getUserLayers to throw NotFoundException
        when(userService.getUserLayers(1L)).thenThrow(new NotFoundException("User layers not found for user with with id: 1"));

        // Call the endpoint and assert that it throws NotFoundException
        assertThrows(NotFoundException.class, () -> {
            userController.getUserLayers(1L);
        });
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
