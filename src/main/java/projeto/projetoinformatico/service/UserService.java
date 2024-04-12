package projeto.projetoinformatico.service;

import org.apache.jena.shared.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import projeto.projetoinformatico.model.Layer;
import projeto.projetoinformatico.users.Role;
import projeto.projetoinformatico.users.User;
import projeto.projetoinformatico.users.UserRepository;

import java.util.List;
import java.util.Optional;

@Service

public class UserService{
    private final UserRepository userRepository;
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public UserDetailsService userDetailsService(){
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return userRepository.findByUsername(username);
            }
        };
    }

    public User createUser(User user) {
        // Additional validation if needed
        return userRepository.save(user);
    }

    @Cacheable(value = "searchCache", key="{#username}")
    public User getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return user;
    }

    @Cacheable(value = "searchCache")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Cacheable(value = "searchCache", key = "{ #role}")
    public List<User> getAllUsersByRole(Role role) {
        return userRepository.findAllByRole(role);
    }

    @Cacheable(value = "searchCache", key = "{ #id }")
    public User getUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }
    public List<Layer> getUserLayers(Long id) {
        return null;
    }
}
