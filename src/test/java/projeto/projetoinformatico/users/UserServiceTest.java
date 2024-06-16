package projeto.projetoinformatico.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import projeto.projetoinformatico.dtos.LayerDTO;
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.exceptions.Exception.InvalidParamsRequestException;
import projeto.projetoinformatico.exceptions.Exception.NotFoundException;
import projeto.projetoinformatico.model.layers.Layer;
import projeto.projetoinformatico.model.layers.LayersRepository;
import projeto.projetoinformatico.model.roleUpgrade.RoleUpgrade;
import projeto.projetoinformatico.model.roleUpgrade.RoleUpgradeRepository;
import projeto.projetoinformatico.model.users.Role;
import projeto.projetoinformatico.model.users.User;
import projeto.projetoinformatico.model.users.UserRepository;
import projeto.projetoinformatico.config.jwt.JWTServiceImpl;
import projeto.projetoinformatico.responses.AuthenticationResponse;
import projeto.projetoinformatico.service.UserService;
import projeto.projetoinformatico.utils.ModelMapperUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private  LayersRepository layersRepository;
    private  RoleUpgradeRepository roleUpgradeRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapperUtils mapperUtils;

    private UserService userService;
    private JWTServiceImpl jwtService;


    @BeforeEach
    public void setUp() {
        layersRepository = mock(LayersRepository.class);
        roleUpgradeRepository = mock(RoleUpgradeRepository.class);
        mapperUtils = mock(ModelMapperUtils.class);
        userRepository = mock(UserRepository.class);
        jwtService = mock(JWTServiceImpl.class);
        userService = new UserService(userRepository, layersRepository, mapperUtils, roleUpgradeRepository);
    }


    @Test
    public void loadUserByUsername_UserFound_Success() {
        String username = "testUser";
        User mockUser = new User();
        mockUser.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(mockUser);
        UserDetails userDetails = userService.loadUserByUsername(username);
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void loadUserByUsername_UserNotFound_ThrowsUsernameNotFoundException() {
        // Arrange
        String username = "nonExistentUser";
        when(userRepository.findByUsername(username)).thenReturn(null);

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(username);
        });

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void getUserByUsername_UserFound_Success() {
        // Arrange
        String username = "testUser";
        User mockUser = new User();
        mockUser.setUsername(username);
        UserDTO expectedUserDTO = new UserDTO();
        expectedUserDTO.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(mockUser);
        when(mapperUtils.userToDTO(mockUser, UserDTO.class)).thenReturn(expectedUserDTO);

        // Act
        UserDTO resultUserDTO = userService.getUserByUsername(username);

        // Assert
        assertNotNull(resultUserDTO);
        assertEquals(expectedUserDTO.getUsername(), resultUserDTO.getUsername());
        verify(userRepository, times(1)).findByUsername(username);
        verify(mapperUtils, times(1)).userToDTO(mockUser, UserDTO.class);
    }

    @Test
    public void getUserByUsername_UserNotFound_ThrowsNotFoundException() {
        // Arrange
        String username = "nonExistentUser";
        when(userRepository.findByUsername(username)).thenReturn(null);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.getUserByUsername(username);
        });
        assertEquals("User not found with username: " + username, exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
        verifyNoInteractions(mapperUtils);
    }

    @Test
    public void getAllUsersByRole_UsersFound_Success() {
        // Arrange
        String role = "USER";
        Role roleEnum = Role.USER;
        List<User> mockUsers = new ArrayList<>();
        mockUsers.add(new User());
        mockUsers.add(new User());
        List<UserDTO> expectedUserDTOs = new ArrayList<>();
        for (User user : mockUsers) {
            expectedUserDTOs.add(new UserDTO());
        }
        when(userRepository.findAllByRole(roleEnum)).thenReturn(mockUsers);
        when(mapperUtils.userToDTO(any(User.class), eq(UserDTO.class))).thenReturn(new UserDTO());

        // Act
        List<UserDTO> resultUserDTOs = userService.getAllUsersByRole(role);

        // Assert
        assertNotNull(resultUserDTOs);
        assertEquals(mockUsers.size(), resultUserDTOs.size());
        for (UserDTO userDTO : resultUserDTOs) {
            assertNotNull(userDTO);
        }
        verify(userRepository, times(1)).findAllByRole(roleEnum);
        verify(mapperUtils, times(mockUsers.size())).userToDTO(any(User.class), eq(UserDTO.class));
    }

    @Test
    public void getAllUsersByRole_NoUsersFound_ThrowsNotFoundException() {
        // Arrange
        String role = "ADMIN";
        Role roleEnum = Role.ADMIN;
        List<User> mockUsers = new ArrayList<>();
        when(userRepository.findAllByRole(roleEnum)).thenReturn(mockUsers);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.getAllUsersByRole(role);
        });
        assertEquals("No users found with role: " + role, exception.getMessage());

        verify(userRepository, times(1)).findAllByRole(roleEnum);
        verifyNoInteractions(mapperUtils);
    }

    @Test
    public void getUserById_UserFound_Success() {
        // Arrange
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);
        UserDTO expectedUserDTO = new UserDTO();
        expectedUserDTO.setId(userId);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(mockUser));
        when(mapperUtils.userToDTO(mockUser, UserDTO.class)).thenReturn(expectedUserDTO);

        // Act
        UserDTO resultUserDTO = userService.getUserById(userId);

        // Assert
        assertNotNull(resultUserDTO);
        assertEquals(expectedUserDTO.getId(), resultUserDTO.getId());
        verify(userRepository, times(1)).findById(userId);
        verify(mapperUtils, times(1)).userToDTO(mockUser, UserDTO.class);
    }

    @Test
    public void getUserById_UserNotFound_ThrowsNotFoundException() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.getUserById(userId);
        });
        assertEquals("User not found with id: " + userId, exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(mapperUtils);
    }

    @Test
    public void getUserLayers_UserLayersFound_Success() {
        // Arrange
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10); // Add pageable argument
        List<Layer> mockLayers = new ArrayList<>();
        mockLayers.add(new Layer());
        mockLayers.add(new Layer());
        Page<Layer> mockLayerPage = new PageImpl<>(mockLayers, pageable, mockLayers.size());
        List<LayerDTO> expectedLayerDTOs = new ArrayList<>();
        for (Layer layer : mockLayers) {
            expectedLayerDTOs.add(new LayerDTO());
        }
        when(layersRepository.findLayersByUserId(userId, pageable)).thenReturn(mockLayerPage);
        when(mapperUtils.map(any(Layer.class), eq(LayerDTO.class))).thenReturn(new LayerDTO());

        // Act
        Page<LayerDTO> resultLayerDTOs = userService.getUserLayers(userId, pageable);

        // Assert
        assertNotNull(resultLayerDTOs);
        assertEquals(mockLayerPage.getTotalElements(), resultLayerDTOs.getTotalElements());
        for (LayerDTO layerDTO : resultLayerDTOs.getContent()) {
            assertNotNull(layerDTO);
        }
        verify(layersRepository, times(1)).findLayersByUserId(userId, pageable);
        verify(mapperUtils, times(mockLayers.size())).map(any(Layer.class), eq(LayerDTO.class));
    }


    @Test
    public void getUserLayers_NoUserLayersFound_ThrowsNotFoundException() {
        // Arrange
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10); // Add pageable argument
        when(layersRepository.findLayersByUserId(userId, pageable)).thenReturn(Page.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.getUserLayers(userId, pageable);
        });
        assertEquals("User layers not found for user with id: " + userId, exception.getMessage());

        verify(layersRepository, times(1)).findLayersByUserId(userId, pageable);
        verifyNoInteractions(mapperUtils);
    }

    @Test
    public void updateUserRole_InvalidRole_ThrowsNotFoundException() {
        // Arrange
        Long userId = 1L;
        String invalidRole = "INVALID_ROLE";
        User mockUser = new User();
        mockUser.setId(userId);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(mockUser));

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.updateUserRole(userId, invalidRole);
        });
        assertEquals("Role not found: " + invalidRole, exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(mapperUtils);
    }

    @Test
    public void deleteOwnUser_UserDeleted_Success() {
        // Arrange
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(mockUser));
        when(layersRepository.findLayersByUserId(userId)).thenReturn(new ArrayList<>());
        when(roleUpgradeRepository.findALlByUserId(userId)).thenReturn(new ArrayList<>());

        // Act
        userService.deleteOwnUser(userId);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(layersRepository, times(1)).findLayersByUserId(userId);
        verify(layersRepository, times(0)).deleteAll(any());
        verify(roleUpgradeRepository, times(1)).findALlByUserId(userId);
        verify(roleUpgradeRepository, times(0)).deleteAll(any());
        verify(userRepository, times(1)).delete(mockUser);
    }

    @Test
    public void deleteOwnUser_UserNotFound_ThrowsNotFoundException() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            userService.deleteOwnUser(userId);
        });

        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(layersRepository);
        verifyNoInteractions(roleUpgradeRepository);
    }

    @Test
    public void deleteOwnUser_LayersExist_Success() {
        // Arrange
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(mockUser));
        List<Layer> mockLayers = new ArrayList<>();
        mockLayers.add(new Layer());
        when(layersRepository.findLayersByUserId(userId)).thenReturn(mockLayers);
        when(roleUpgradeRepository.findALlByUserId(userId)).thenReturn(new ArrayList<>());

        // Act
        userService.deleteOwnUser(userId);

        // Assert
        verify(layersRepository, times(1)).deleteAll(mockLayers);
        verify(roleUpgradeRepository, times(1)).findALlByUserId(userId);
        verify(roleUpgradeRepository, times(0)).deleteAll(any());
        verify(userRepository, times(1)).delete(mockUser);
    }

    @Test
    public void deleteOwnUser_RoleUpgradeRequestsExist_Success() {
        // Arrange
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(mockUser));
        when(layersRepository.findLayersByUserId(userId)).thenReturn(new ArrayList<>());
        List<RoleUpgrade> mockRoleUpgradeRequests = new ArrayList<>();
        mockRoleUpgradeRequests.add(new RoleUpgrade());
        when(roleUpgradeRepository.findALlByUserId(userId)).thenReturn(mockRoleUpgradeRequests);

        // Act
        userService.deleteOwnUser(userId);

        // Assert
        verify(layersRepository, times(1)).findLayersByUserId(userId);
        verify(layersRepository, times(0)).deleteAll(any());
        verify(roleUpgradeRepository, times(1)).deleteAll(mockRoleUpgradeRequests);
        verify(userRepository, times(1)).delete(mockUser);
    }


    @Test
    public void testBlockUser() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setRole(Role.USER);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        userService.blockUser(userId);

        // Assert
        assertFalse(user.isAccountNonLocked());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }


    @Test
    public void testDeleteUser() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setRole(Role.USER);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).delete(user);
    }
    @Test
    public void updateUserUsername_UserFound_Success() {
        // Arrange
        String username = "testUser";
        String newUsername = "newTestUser";
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setPassword("oldPassword");
        user.setEmail("oldemail@example.com");
        user.setRole(Role.USER);
        user.setAccountNonLocked(true); // Or false if needed
        when(userRepository.findByUsername(username)).thenReturn(user);
        when(userRepository.existsByUsername(newUsername)).thenReturn(false);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);

        // Mock the behavior of ModelMapperUtils
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(newUsername);
        when(mapperUtils.userToDTO(user, UserDTO.class)).thenReturn(userDTO);

        // Act
        user.setUsername(newUsername); // Update the user object with the new username
        AuthenticationResponse response = userService.updateUserUsername(username, newUsername);

        // Assert
        assertNotNull(response);
        assertEquals(newUsername, response.getUser().getUsername());
    }



    @Test
    public void updateUserUsername_UsernameAlreadyExists_ThrowsInvalidParamsRequestException() {
        // Arrange
        String username = "testUser";
        String newUsername = "existingUser";
        when(userRepository.findByUsername(username)).thenReturn(new User());
        when(userRepository.existsByUsername(newUsername)).thenReturn(true);

        // Act & Assert
        InvalidParamsRequestException exception = assertThrows(InvalidParamsRequestException.class, () -> {
            userService.updateUserUsername(username, newUsername);
        });
        assertEquals("Username already exists", exception.getMessage());
    }
    @Test
    public void updateUserEmail_UserFound_Success() {
        // Arrange
        String username = "testUser";
        String newEmail = "newemail@example.com";
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setPassword("oldPassword");
        user.setEmail("oldemail@example.com");
        user.setRole(Role.USER);
        user.setAccountNonLocked(true); // Or false if needed
        when(userRepository.findByUsername(username)).thenReturn(user);
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(newEmail)).thenReturn(false);

        // Mocking the behavior of mapperUtils to return a non-null UserDTO with the updated email
        UserDTO mockedUserDTO = new UserDTO();
        mockedUserDTO.setEmail(newEmail);
        when(mapperUtils.userToDTO(user, UserDTO.class)).thenReturn(mockedUserDTO);

        // Act
        AuthenticationResponse response = userService.updateUserEmail(username, newEmail);

        // Assert
        assertNotNull(response);
        assertEquals(newEmail, response.getUser().getEmail());
    }



    @Test
    public void getAllUsersByRolePaged_UsersFound_Success() {
        // Arrange
        String role = "USER";
        Pageable pageable = PageRequest.of(0, 10);
        List<User> mockUsers = new ArrayList<>();
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setRole(Role.USER); // Ensure role is set
        user1.setAccountNonLocked(true); // Ensure accountNonLocked is set
        mockUsers.add(user1);

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setRole(Role.USER); // Ensure role is set
        user2.setAccountNonLocked(true); // Ensure accountNonLocked is set
        mockUsers.add(user2);

        Page<User> mockUserPage = new PageImpl<>(mockUsers, pageable, mockUsers.size());
        when(userRepository.findAllByRole(Role.valueOf(role), pageable)).thenReturn(mockUserPage);

        // Mocking the behavior of mapperUtils.userToDTO to return non-null UserDTO objects
        when(mapperUtils.userToDTO(any(User.class), eq(UserDTO.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    UserDTO userDTO = new UserDTO();
                    userDTO.setId(user.getId());
                    userDTO.setUsername(user.getUsername());
                    userDTO.setEmail(user.getEmail());
                    userDTO.setRole(user.getRole().toString());
                    // Set other properties as needed
                    return userDTO;
                });

        // Act
        Page<UserDTO> resultUserDTOPage = userService.getAllUsersByRolePaged(role, pageable);

        // Assert
        assertNotNull(resultUserDTOPage);
        assertEquals(mockUserPage.getTotalElements(), resultUserDTOPage.getTotalElements());

        // Verify that mapperUtils.userToDTO was invoked for each user in the page
        mockUsers.forEach(user -> {
            verify(mapperUtils, times(1)).userToDTO(eq(user), eq(UserDTO.class));
        });
    }



   }
