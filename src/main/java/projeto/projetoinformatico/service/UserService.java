package projeto.projetoinformatico.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import projeto.projetoinformatico.exceptions.Exception.NotFoundException;
import projeto.projetoinformatico.model.layers.Layer;
import projeto.projetoinformatico.model.layers.LayersRepository;
import projeto.projetoinformatico.model.users.Role;
import projeto.projetoinformatico.model.users.User;
import projeto.projetoinformatico.model.users.UserRepository;
import projeto.projetoinformatico.responses.UserResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final LayersRepository layersRepository;

    @Autowired
    public UserService(UserRepository userRepository, LayersRepository layersRepository) {
        this.userRepository = userRepository;
        this.layersRepository = layersRepository;
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
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new NotFoundException("User not found with username: " + username);
        }
        return convertUserToUserResponse(user);
    }

    @Cacheable(value = "searchCache")
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertUserToUserResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "searchCache", key = "#role")
    public List<UserResponse> getAllUsersByRole(Role role) {
        List<User> users = userRepository.findAllByRole(role);
        if (users.isEmpty()) {
            throw new NotFoundException("No users found with role: " + role);
        }
        return users.stream()
                .map(this::convertUserToUserResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "searchCache", key = "#id")
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        return convertUserToUserResponse(user);
    }

    public List<Layer> getUserLayers(String username) {
        List<Layer> layers = layersRepository.findByUsername(username);
        if (layers.isEmpty()) {
            throw new NotFoundException("User layers not found for user with username: " + username);
        }
        return layers;
    }

    public String getUsernameById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        return (user != null) ? user.getUsername() : null;
    }

    public List<UserResponse> getUsersByNameAndRole(String name, Role role) {
        List<User> users = userRepository.findByUsernameStartingWithIgnoreCaseAndRole(name, role);
        if (users.isEmpty()) {
            throw new NotFoundException("No users found with name starting with: " + name + " and role: " + role);
        }
        return users.stream()
                .map(this::convertUserToUserResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getUserContainingUsername(String name) {
        List<User> users = userRepository.findByUsernameStartingWithIgnoreCase(name);
        if (users.isEmpty()) {
            throw new NotFoundException("No users found with name starting with: " + name);
        }
        return users.stream()
                .map(this::convertUserToUserResponse)
                .collect(Collectors.toList());
    }

    private UserResponse convertUserToUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setRole(user.getRole());
        userResponse.setEmail(user.getEmail());
        userResponse.setEnabled(user.isEnabled());
        // Set other fields as needed
        return userResponse;
    }

}
