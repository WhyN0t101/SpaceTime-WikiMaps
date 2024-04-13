package projeto.projetoinformatico.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import projeto.projetoinformatico.Exceptions.NotFoundException;
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
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return userRepository.findByUsername(username);
            }
        };
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return user;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Cacheable(value = "searchCache")
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Cacheable(value = "searchCache")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Cacheable(value = "searchCache", key = "{ #role }")
    public List<User> getAllUsersByRole(Role role) {
        return userRepository.findAllByRole(role);
    }

    @Cacheable(value = "searchCache", key = "{ #id }")
    public User getUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }



    public List<Layer> getUserLayers(String username) {
        return layersRepository.findByUsername(username);
    }

    public String getUsernameById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        return (user != null) ? user.getUsername() : null;
    }
}
