package projeto.projetoinformatico;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import projeto.projetoinformatico.users.Role;
import projeto.projetoinformatico.users.User;
import projeto.projetoinformatico.users.UserRepository;

@SpringBootApplication
@EnableCaching
@EntityScan("projeto.projetoinformatico.users")
public class ProjetoInformaticoApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(ProjetoInformaticoApplication.class, args);
    }

    public void run(String... args){
        User adminAccount = userRepository.findByRole(Role.ADMIN);
        if(adminAccount == null){
            User user = new User();

            user.setEmail("admin@mail.com");
            user.setUsername("Admin");
            user.setRole(Role.ADMIN);
            user.setPassword(new BCryptPasswordEncoder().encode("admin"));

            userRepository.save(user);

        }
    }
}
