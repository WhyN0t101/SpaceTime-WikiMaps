package projeto.projetoinformatico.service;

import org.apache.jena.shared.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import projeto.projetoinformatico.model.layers.Layer;
import projeto.projetoinformatico.model.layers.LayersRepository;
import projeto.projetoinformatico.model.users.Role;
import projeto.projetoinformatico.model.users.User;
import projeto.projetoinformatico.model.users.UserRepository;

import java.util.List;
import java.util.Optional;

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


    @Cacheable(value = "searchCache", key="{#username}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public User getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new NotFoundException("User not found with username: " + username);
        }
        return user;
    }

    @Cacheable(value = "searchCache")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Cacheable(value = "searchCache", key = "{ #role}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public List<User> getAllUsersByRole(Role role) {
        return userRepository.findAllByRole(role);
    }

    @Cacheable(value = "searchCache", key = "{ #id }")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public User getUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }

    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
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
}
