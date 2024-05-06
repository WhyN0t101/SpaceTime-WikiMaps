package projeto.projetoinformatico.utils;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import projeto.projetoinformatico.model.users.Role;
import projeto.projetoinformatico.model.users.User;
import projeto.projetoinformatico.model.users.UserRepository;

@Component
public class AdminUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    public AdminUserInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        // Check if an admin user already exists
        if (!userRepository.existsByUsername("Admin")) {
            // Create a new admin user
            User adminUser = new User();
            adminUser.setUsername("Admin");
            adminUser.setEmail("admin@mail.com");
            adminUser.setRole(Role.ADMIN);
            adminUser.setPassword(new BCryptPasswordEncoder().encode("admin"));

            // Save the admin user to the database
            userRepository.save(adminUser);
        }
    }
}
