package projeto.projetoinformatico.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import projeto.projetoinformatico.dtos.LayerDTO;
import projeto.projetoinformatico.dtos.RoleUpgradeDTO;
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.exceptions.Exception.NotFoundException;
import projeto.projetoinformatico.model.layers.Layer;
import projeto.projetoinformatico.model.layers.LayersRepository;
import projeto.projetoinformatico.model.roleUpgrade.RoleUpgrade;
import projeto.projetoinformatico.model.roleUpgrade.RoleUpgradeRepository;
import projeto.projetoinformatico.model.users.Role;
import projeto.projetoinformatico.model.users.User;
import projeto.projetoinformatico.model.users.UserRepository;
import projeto.projetoinformatico.responses.AuthenticationResponse;
import projeto.projetoinformatico.service.JWT.JWTServiceImpl;
import projeto.projetoinformatico.utils.ModelMapperUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final LayersRepository layersRepository;
    private final RoleUpgradeRepository roleUpgradeRepository;
    private final ModelMapper modelMapper;
    private final ModelMapperUtils mapperUtils;


    @Autowired
    public UserService(UserRepository userRepository, LayersRepository layersRepository,ModelMapper modelMapper, ModelMapperUtils mapperUtils, RoleUpgradeRepository roleUpgradeRepository) {
        this.userRepository = userRepository;
        this.layersRepository = layersRepository;
        this.modelMapper = modelMapper;
        this.mapperUtils = mapperUtils;
        this.roleUpgradeRepository = roleUpgradeRepository;
    }
    public UserDetailsService userDetailsService(){
        return userRepository::findByUsername;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return user;
    }

    @Cacheable(value = "searchCache", key = "#username")
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new NotFoundException("User not found with username: " + username);
        }
        UserDTO userDTO = convertUserToDTO(user);
        return userDTO;
    }


    @Cacheable(value = "userCache")
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertUserToDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "userCache", key = "#role")
    public List<UserDTO> getAllUsersByRole(String role) {
        try {
            Role roleEnum = Role.valueOf(role.toUpperCase());
            List<User> users = userRepository.findAllByRole(roleEnum);
            if (users.isEmpty()) {
                throw new NotFoundException("No users found with role: " + role);
            }
            return users.stream()
                    .map(this::convertUserToDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Role not found: " + role);
        }
    }

    @Cacheable(value = "userCache", key = "#id")
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        UserDTO userDTO = convertUserToDTO(user);


        return userDTO;
    }

    @Cacheable(value = "layerCache", key = "#id")
    public List<LayerDTO> getUserLayers(Long id) {
        List<Layer> layers = layersRepository.findLayersByUserId(id);
        if (layers.isEmpty()) {
            throw new NotFoundException("User layers not found for user with id: " + id);
        }
        return layers.stream()
                .map(this::convertLayerToDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "userCache", key = "#name")
    public List<UserDTO> getUsersByNameAndRole(String name, String role) {
        try {
            Role roleEnum = Role.valueOf(role.toUpperCase());
            List<User> users = userRepository.findByUsernameStartingWithIgnoreCaseAndRole(name, roleEnum);
            if (users.isEmpty()) {
                throw new NotFoundException("No users found with name starting with: " + name + " and role: " + role);
            }
            return users.stream()
                    .map(this::convertUserToDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Role not found: " + role);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Role cannot be null");
        }
    }


    @Cacheable(value = "userCache", key = "#name")
    public List<UserDTO> getUserContainingUsername(String name) {
        List<User> users = userRepository.findByUsernameStartingWithIgnoreCase(name);
        if (users.isEmpty()) {
            throw new NotFoundException("No users found with name starting with: " + name);
        }
        return users.stream()
                .map(this::convertUserToDTO)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "userCache", key = "#id")
    public UserDTO updateUserRole(Long id, String role) {
        User user = userRepository.findUserById(id);
        if (user == null) {
            throw new NotFoundException("User not found with id: " + id);
        }

        try {
            Role roleEnum = Role.valueOf(role.toUpperCase());
            user.setRole(roleEnum);
            userRepository.save(user);
            return convertUserToDTO(user);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Role not found: " + role);
        }
    }

    @CacheEvict(value = "userCache", key = "#newUsername")
    public AuthenticationResponse updateUserUsernameEmail(String username, String newUsername, String newEmail) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new NotFoundException("User not found with username: " + username);
        }
        try {
            user.setUsername(newUsername);
            user.setEmail(newEmail);
            var jwt = JWTServiceImpl.generateToken(user);
            var refreshToken = JWTServiceImpl.generateRefreshToken(new HashMap<>(), user);
            AuthenticationResponse response = new AuthenticationResponse();
            response.setAccessToken(jwt);
            response.setRefreshToken(refreshToken);
            UserDTO userDTO = convertUserToDTO(user);
            userRepository.save(user);
            response.setUser(userDTO);
            return response;
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Error altering email and username");
        }
    }

    @CacheEvict(value = "userCache", key = "#newUsername")
    public AuthenticationResponse updateUserUsername(String username, String newUsername) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new NotFoundException("User not found with username: " + username);
        }
        try {
            user.setUsername(newUsername);
            var jwt = JWTServiceImpl.generateToken(user);
            var refreshToken = JWTServiceImpl.generateRefreshToken(new HashMap<>(), user);
            AuthenticationResponse response = new AuthenticationResponse();
            response.setAccessToken(jwt);
            response.setRefreshToken(refreshToken);
            UserDTO userDTO = convertUserToDTO(user);
            userRepository.save(user);
            response.setUser(userDTO);
            return response;
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Error altering username");
        }
    }


    @CacheEvict(value = "userCache", key = "#newEmail")
    public AuthenticationResponse updateUserEmail(String username, String newEmail) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new NotFoundException("User not found with username: " + username);
        }
        try {
            user.setEmail(newEmail);
            userRepository.save(user);
            AuthenticationResponse response = new AuthenticationResponse();
            UserDTO userDTO = convertUserToDTO(user);
            response.setUser(userDTO);
            return response;
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Error altering email");
        }
    }

    private UserDTO convertUserToDTO(User user) {
        UserDTO dto = mapperUtils.userToDTO(user, UserDTO.class);
        RoleUpgrade roleUpgrade = roleUpgradeRepository.findByUserId(user.getId());
        if (roleUpgrade != null) {
            RoleUpgradeDTO roleUpgradeDTO = new RoleUpgradeDTO();
            // Populate RoleUpgradeDTO from RoleUpgrade entity
            // You can use a similar approach as shown in the previous response
            dto.setRoleUpgrade(roleUpgrade);
        }
        return dto;
    }
    private LayerDTO convertLayerToDTO(Layer layer) {
        return mapperUtils.layerToDTO(layer, LayerDTO.class);
    }



    @CacheEvict(value = "userCache", key = "#userId")
    public void deleteUser(Long userId) {
        // Find the user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        // Delete associated layers
        deleteLayersByUserId(userId);
        // Delete associated role upgrade requests
        deleteRoleUpgradeRequestsByUserId(userId);

        // Delete the user
        userRepository.delete(user);
    }

    private void deleteLayersByUserId(Long userId) {
        List<Layer> layers = layersRepository.findLayersByUserId(userId);
        if (!layers.isEmpty()) {
            layersRepository.deleteAll(layers);
        }
    }

    private void deleteRoleUpgradeRequestsByUserId(Long userId) {
        List<RoleUpgrade> roleUpgradeRequests = roleUpgradeRepository.findALlByUserId(userId);
        if (!roleUpgradeRequests.isEmpty()) {
            roleUpgradeRepository.deleteAll(roleUpgradeRequests);
        }
    }
}
