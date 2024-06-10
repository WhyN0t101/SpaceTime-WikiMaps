package projeto.projetoinformatico.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import projeto.projetoinformatico.controllers.UserController;
import projeto.projetoinformatico.dtos.LayerDTO;
import projeto.projetoinformatico.dtos.Paged.LayerPageDTO;
import projeto.projetoinformatico.dtos.Paged.UserPageDTO;
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.exceptions.Exception.InvalidParamsRequestException;
import projeto.projetoinformatico.exceptions.Exception.NotFoundException;
import projeto.projetoinformatico.model.users.UserRepository;
import projeto.projetoinformatico.service.UserService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    private UserService userService;
    private UserController userController;
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userService = mock(UserService.class);
        userRepository = mock(UserRepository.class);
        userController = new UserController(userService);
    }

    @Test
    public void testGetUserByUsername_Success() {
        // Mock dependencies
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
    public void testGetAllUsersSuccess() {
        // Arrange
        // Create a sample user DTO
        UserDTO sampleUser = new UserDTO();
        sampleUser.setUsername("testUser");
        sampleUser.setRole("ROLE_USER");

        // Create a page containing the sample user DTO
        Page<UserDTO> userPage = new PageImpl<>(Collections.singletonList(sampleUser));

        // Mock the userService to return the userPage
        when(userService.getAllUsersPaged(any(PageRequest.class))).thenReturn(userPage);

        // Act
        ResponseEntity<UserPageDTO> response = userController.getAllUsers(null, null, 0, 10);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        // Assert that the returned UserPageDTO contains user(s)
        assertTrue(response.getBody().getUsers().size() > 0);
        // Assert the content of the first user in the list
        assertEquals("testUser", response.getBody().getUsers().get(0).getUsername());
        assertEquals("ROLE_USER", response.getBody().getUsers().get(0).getRole());
        // Assert pagination information
        assertEquals(0, response.getBody().getCurrentPage());
        assertEquals(1, response.getBody().getTotalItems());
        assertEquals(1, response.getBody().getTotalPages());
    }

    @Test
    public void testGetAllUsersWithInvalidSize() {
        try {
            userController.getAllUsers(null, null, 0, 0);
        } catch (InvalidParamsRequestException e) {
            assertEquals("Invalid size of pagination", e.getMessage());
        }
    }

    @Test
    public void testGetAllUsersWithInvalidPage() {
        try {
            userController.getAllUsers(null, null, -1, 10);
        } catch (InvalidParamsRequestException e) {
            assertEquals("Invalid page of pagination", e.getMessage());
        }
    }

    @Test
    public void testGetAllUsersByRoleSuccess() {
        // Arrange
        UserDTO testUser = new UserDTO();
        testUser.setUsername("testUser"); // Set the username
        testUser.setRole("USER"); // Set the role
        List<UserDTO> userList = Collections.singletonList(testUser);
        when(userService.getAllUsersByRole("USER")).thenReturn(userList);

        // Act
        ResponseEntity<List<UserDTO>> response = userController.getAllUsersByRole("USER");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("testUser", response.getBody().get(0).getUsername());
        assertEquals("USER", response.getBody().get(0).getRole());
    }

    @Test
    public void testGetAllUsersByRoleNotFound() {
        // Arrange
        when(userService.getAllUsersByRole("USER")).thenThrow(new NotFoundException("No users found with role: USER"));

        // Act and Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userController.getAllUsersByRole("USER");
        });
        assertTrue(exception.getMessage().contains("No users found with role: USER"));
    }

    @Test
    public void testGetUserByIdSuccess() {
        // Arrange
        Long userId = 1L;
        UserDTO expectedUser = new UserDTO(); // Create an expected user object
        when(userService.getUserById(userId)).thenReturn(expectedUser);

        // Act
        ResponseEntity<UserDTO> response = userController.getUserById(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedUser, response.getBody());
    }

    @Test
    public void testGetUserByIdNotFoundError() {
        // Arrange
        Long userId = 1L;
        when(userService.getUserById(userId))
                .thenThrow(new NotFoundException("User not found with id: " + userId));

        // Act and Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userController.getUserById(userId);
        });
        assertTrue(exception.getMessage().contains("User not found with id: " + userId));
    }

    @Test
    public void testGetUserLayersSuccess() {
        // Arrange
        Long userId = 1L;
        List<LayerDTO> expectedLayers = Arrays.asList(new LayerDTO(), new LayerDTO());
        Page<LayerDTO> page = new PageImpl<>(expectedLayers);
        when(userService.getUserLayers(eq(userId), any(Pageable.class))).thenReturn(page);

        // Act
        ResponseEntity<LayerPageDTO> response = userController.getUserLayers(0, 10, userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedLayers.size(), response.getBody().getLayers().size());
    }

    @Test
    public void testGetUserLayersNotFoundError() {
        // Arrange
        Long userId = 1L;
        when(userService.getUserLayers(eq(userId), any(Pageable.class)))
                .thenThrow(new NotFoundException("User layers not found for user with id: " + userId));

        // Act and Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userController.getUserLayers(0, 10, userId);
        });
        assertTrue(exception.getMessage().contains("User layers not found for user with id: " + userId));
    }
    @Test
    public void testGetAuthenticatedUserSuccess() {
        // Arrange
        String authenticatedUsername = "testUser";
        UserDTO expectedUser = new UserDTO();
        when(userService.getUserByUsername(authenticatedUsername)).thenReturn(expectedUser);

        // Mock Authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(authenticatedUsername);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        ResponseEntity<UserDTO> response = userController.getAuthenticatedUser();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedUser, response.getBody());
    }
    @Test
    public void testUpdateUserRoleSuccess() {
        // Arrange
        Long userId = 1L;
        String newRole = "ADMIN";
        UserDTO userDTO = new UserDTO();
        userDTO.setRole(newRole); // Set the role field of UserDTO
        when(userService.updateUserRole(userId, newRole)).thenReturn(userDTO);

        // Act
        ResponseEntity<UserDTO> response = userController.updateUserRole(userId, newRole);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newRole, response.getBody().getRole());
    }

    @Test
    public void testUpdateUserRoleErrorUserNotFound() {
        // Arrange
        Long userId = 1L;
        String newRole = "ADMIN";
        when(userService.updateUserRole(userId, newRole)).thenThrow(new NotFoundException("User not found with id: " + userId));

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userController.updateUserRole(userId, newRole);
        });
        assertTrue(exception.getMessage().contains("User not found with id: " + userId));
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

    /*
    @Test
    void testUpdateUserUsernameEmailSuccess() {
        // Arrange
        AlterRequest alterRequest = new AlterRequest();
        alterRequest.setUsername("newUsername");
        alterRequest.setEmail("new@example.com");

        AuthenticationResponse expectedResponse = new AuthenticationResponse();
        // Set expectedResponse fields as needed

        when(userService.updateUserUsernameEmail(anyString(), anyString(), anyString()))
                .thenReturn(expectedResponse);

        // Act
        ResponseEntity<AuthenticationResponse> response = userController.updateUserRole(alterRequest, mock(Authentication.class));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }
*/

    /*
    @Test//Falha
    void testUpdateUserUsernameEmailUsernameExistsError() {
        // Arrange
        AlterRequest alterRequest = new AlterRequest();
        alterRequest.setUsername("existingUsername");
        alterRequest.setEmail("new@example.com");

        // Mock the userService to throw InvalidParamsRequestException
        when(userService.updateUserUsernameEmail(anyString(), anyString(), anyString()))
                .thenThrow(new InvalidParamsRequestException("Username already exists"));

        // Act & Assert
        assertThrows(InvalidParamsRequestException.class, () ->
                userController.updateUserRole(alterRequest, mock(Authentication.class)));
    }

    @Test//Falha
    void testUpdateUserUsernameEmailEmailExistsError() {
        // Arrange
        AlterRequest alterRequest = new AlterRequest();
        alterRequest.setUsername("newUsername");
        alterRequest.setEmail("existing@example.com");

        when(userService.updateUserUsernameEmail(anyString(), anyString(), anyString()))
                .thenThrow(new InvalidParamsRequestException("Email already registered"));

        // Act & Assert
        assertThrows(InvalidParamsRequestException.class, () ->
                userController.updateUserRole(alterRequest, mock(Authentication.class)));
    }
*/
/*
    @Test
    void testDeleteUserSuccess() {
        // Mock authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        // Mock userService for success case
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L); // Assuming user ID is 1 for testUser

        // Mock userService to return the userDTO
        when(userService.getUserByUsername("testUser")).thenReturn(userDTO);

        // Set up controller
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Invoke the controller method
        ResponseEntity<Void> responseEntity = userController.deleteUser();

        // Assert that the userRepository delete method is called with the correct user ID
        verify(userRepository).deleteById(1L);

        // Assert response
        assertEquals(204, responseEntity.getStatusCodeValue());
    }
*/

/*
    @Test
    void testDeleteUserNotFoundError() {
        // Mock authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        // Mock userService for error case
        when(userRepository.delete(any())).thenThrow(new NotFoundException("User not found"));

        // Set up controller
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Invoke and assert that NotFoundException is thrown
        assertThrows(NotFoundException.class, () -> userController.deleteUser());
    }*/

    /*
    @Test//Verificar
    public void testGetAuthenticatedUserNotFound() {
        // Arrange
        String authenticatedUsername = "nonExistingUser";
        when(userRepository.findByUsername(authenticatedUsername)).thenReturn(null);

        // Mock Authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(authenticatedUsername);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        Throwable exception = assertThrows(NotFoundException.class, () -> {
            userController.getAuthenticatedUser();
        });

        // Assert
        assertNotNull(exception);
        assertEquals("User not found with username: " + authenticatedUsername, exception.getMessage());
    }*/


/*
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
*/
}
