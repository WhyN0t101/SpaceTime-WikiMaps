package projeto.projetoinformatico.service;

import org.eclipse.rdf4j.http.protocol.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projeto.projetoinformatico.dtos.LayerDTO;
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.exceptions.Exception.InvalidRequestException;
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
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final LayersRepository layersRepository;
    private final RoleUpgradeRepository roleUpgradeRepository;
    private final ModelMapperUtils mapperUtils;

    @Autowired
    public UserService(UserRepository userRepository, LayersRepository layersRepository, ModelMapperUtils mapperUtils, RoleUpgradeRepository roleUpgradeRepository) {
        this.userRepository = userRepository;
        this.layersRepository = layersRepository;
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
        return convertUserToDTO(user);
    }



    @Cacheable(value = "userCache")
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertUserToDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "userCache", key = "#role")
    public List<UserDTO> getAllUsersByRole(String role) {
        Role roleEnum = getRoleEnum(role);
        List<User> users = userRepository.findAllByRole(roleEnum);
        if (users.isEmpty()) {
            throw new NotFoundException("No users found with role: " + role);
        }
        return users.stream()
                .map(this::convertUserToDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "userCache", key = "#id")
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        return convertUserToDTO(user);
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
        Role roleEnum = getRoleEnum(role);
        List<User> users = userRepository.findByUsernameStartingWithIgnoreCaseAndRole(name, roleEnum);
        if (users.isEmpty()) {
            throw new NotFoundException("No users found with name starting with: " + name + " and role: " + role);
        }
        return users.stream()
                .map(this::convertUserToDTO)
                .collect(Collectors.toList());
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
        User user = findUserById(id);
        Role roleEnum = getRoleEnum(role);
        user.setRole(roleEnum);
        userRepository.save(user);
        return convertUserToDTO(user);
    }

    @CacheEvict(value = "userCache", key = "#newUsername")
    public AuthenticationResponse updateUserUsernameEmail(String username, String newUsername, String newEmail) {
        User user = findUserByUsername(username);
        user.setUsername(newUsername);
        user.setEmail(newEmail);
        return generateAuthenticationResponse(user);
    }

    @CacheEvict(value = "userCache", key = "#newUsername")
    public AuthenticationResponse updateUserUsername(String username, String newUsername) {
        User user = findUserByUsername(username);
        user.setUsername(newUsername);
        return generateAuthenticationResponse(user);
    }

    @CacheEvict(value = "userCache", key = "#newEmail")
    public AuthenticationResponse updateUserEmail(String username, String newEmail) {
        User user = findUserByUsername(username);
        user.setEmail(newEmail);
        userRepository.save(user);
        AuthenticationResponse response = new AuthenticationResponse();
        response.setUser(convertUserToDTO(user));
        return response;
    }

    @CacheEvict(value = "userCache", key = "#userId")
    @Transactional
    public void deleteUser(Long userId) {
        User targetUser = findUserById(userId);
        // Ensure an admin cannot delete another admin
        if (targetUser.getRole() == Role.ADMIN) {
            throw new InvalidRequestException("Admin users cannot delete other admin users.");
        }
        deleteLayersByUserId(userId);
        deleteRoleUpgradeRequestsByUserId(userId);
        userRepository.delete(targetUser);
    }
    @CacheEvict(value = "userCache", key = "#userId")
    @Transactional
    public void deleteOwnUser(Long userId) {
        User targetUser = findUserById(userId);
        deleteLayersByUserId(userId);
        deleteRoleUpgradeRequestsByUserId(userId);
        userRepository.delete(targetUser);
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

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }

    private User findUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new NotFoundException("User not found with username: " + username);
        }
        return user;
    }


    private Role getRoleEnum(String role) {
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Role not found: " + role);
        }
    }

    private AuthenticationResponse generateAuthenticationResponse(User user) {
        String jwt = JWTServiceImpl.generateToken(user);
        String refreshToken = JWTServiceImpl.generateRefreshToken(new HashMap<>(), user);
        AuthenticationResponse response = new AuthenticationResponse();
        response.setAccessToken(jwt);
        response.setRefreshToken(refreshToken);
        response.setUser(convertUserToDTO(user));
        userRepository.save(user);
        return response;
    }

    private UserDTO convertUserToDTO(User user) {
        UserDTO dto = mapperUtils.userToDTO(user, UserDTO.class);
        RoleUpgrade roleUpgrade = roleUpgradeRepository.findByUserId(user.getId());
        if (roleUpgrade != null) {
            dto.setRoleUpgrade(roleUpgrade);
        }
        return dto;
    }

    private LayerDTO convertLayerToDTO(Layer layer) {
        return mapperUtils.layerToDTO(layer, LayerDTO.class);
    }
}
