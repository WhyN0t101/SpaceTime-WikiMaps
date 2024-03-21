package projeto.projetoinformatico.service;

import org.springframework.stereotype.Service;
import projeto.projetoinformatico.users.User;
import projeto.projetoinformatico.users.UserRepository;

@Service

public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        // Additional validation if needed
        return userRepository.save(user);
    }
    /*
    public User createUser(User user) {
        // Create a new User object
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(user.getPassword());

        // Save the user to the database
        return userRepository.save(newUser);
    }
    */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

}
