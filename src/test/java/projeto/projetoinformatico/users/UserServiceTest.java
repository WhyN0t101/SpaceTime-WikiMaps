package projeto.projetoinformatico.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import projeto.projetoinformatico.controllers.UserController;
import projeto.projetoinformatico.dtos.LayerDTO;
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.exceptions.Exception.NotFoundException;
import projeto.projetoinformatico.model.layers.Layer;
import projeto.projetoinformatico.model.layers.LayersRepository;
import projeto.projetoinformatico.model.roleUpgrade.RoleStatus;
import projeto.projetoinformatico.model.roleUpgrade.RoleUpgrade;
import projeto.projetoinformatico.model.roleUpgrade.RoleUpgradeRepository;
import projeto.projetoinformatico.model.users.Role;
import projeto.projetoinformatico.model.users.User;
import projeto.projetoinformatico.model.users.UserRepository;
import projeto.projetoinformatico.service.UserService;
import projeto.projetoinformatico.utils.ModelMapperUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private  UserRepository userRepository;
    private  LayersRepository layersRepository;
    private  RoleUpgradeRepository roleUpgradeRepository;
    private  ModelMapperUtils mapperUtils;
    private  UserService userService;


    @BeforeEach
    public void setUp() {
        layersRepository = mock(LayersRepository.class);
        roleUpgradeRepository = mock(RoleUpgradeRepository.class);
        mapperUtils = mock(ModelMapperUtils.class);
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository, layersRepository, mapperUtils, roleUpgradeRepository);
    }


    @Test
    public void loadUserByUsername_UserFound_Success() {
        // Arrange
        String username = "testUser";
        User mockUser = new User();
        mockUser.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(mockUser);

        // Act
        UserDetails userDetails = userService.loadUserByUsername(username);

        // Assert
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
        List<Layer> mockLayers = new ArrayList<>();
        mockLayers.add(new Layer());
        mockLayers.add(new Layer());
        List<LayerDTO> expectedLayerDTOs = new ArrayList<>();
        for (Layer layer : mockLayers) {
            expectedLayerDTOs.add(new LayerDTO());
        }
        when(layersRepository.findLayersByUserId(userId)).thenReturn(mockLayers);
        when(mapperUtils.map(any(Layer.class), eq(LayerDTO.class))).thenReturn(new LayerDTO());

        // Act
        List<LayerDTO> resultLayerDTOs = userService.getUserLayers(userId);

        // Assert
        assertNotNull(resultLayerDTOs);
        assertEquals(mockLayers.size(), resultLayerDTOs.size());
        for (LayerDTO layerDTO : resultLayerDTOs) {
            assertNotNull(layerDTO);
        }
        verify(layersRepository, times(1)).findLayersByUserId(userId);
        verify(mapperUtils, times(mockLayers.size())).map(any(Layer.class), eq(LayerDTO.class));
    }

    @Test
    public void getUserLayers_NoUserLayersFound_ThrowsNotFoundException() {
        // Arrange
        Long userId = 1L;
        when(layersRepository.findLayersByUserId(userId)).thenReturn(new ArrayList<>());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.getUserLayers(userId);
        });
        assertEquals("User layers not found for user with id: " + userId, exception.getMessage());

        verify(layersRepository, times(1)).findLayersByUserId(userId);
        verifyNoInteractions(mapperUtils);
    }

    @Test
    public void updateUserRole_UserRoleUpdated_Success() {
        // Arrange
        Long userId = 1L;
        String newRole = "ADMIN";
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setRole(Role.USER); // Original role
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(mockUser));
        when(mapperUtils.userToDTO(any(User.class), eq(UserDTO.class))).thenReturn(new UserDTO());

        // Act
        UserDTO resultUserDTO = userService.updateUserRole(userId, newRole);

        // Assert
        assertNotNull(resultUserDTO);
        assertEquals(userId, resultUserDTO.getId());
        assertEquals(newRole, resultUserDTO.getRole());
        verify(userRepository, times(1)).save(mockUser);
        verify(userRepository, times(1)).findById(userId);
        verify(mapperUtils, times(1)).userToDTO(mockUser, UserDTO.class);
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
}
