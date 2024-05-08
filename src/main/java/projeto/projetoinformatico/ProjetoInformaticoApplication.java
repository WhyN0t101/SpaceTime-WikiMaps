package projeto.projetoinformatico;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import projeto.projetoinformatico.model.users.Role;
import projeto.projetoinformatico.model.users.User;
import projeto.projetoinformatico.model.users.UserRepository;

import java.util.List;

@SpringBootApplication
@EnableCaching
@EntityScan(basePackages = {"projeto.projetoinformatico.model.users", "projeto.projetoinformatico.model.layers", "projeto.projetoinformatico.model.roleUpgrade"})
//@EnableJpaRepositories(basePackages = "projeto.projetoinformatico.model")
public class ProjetoInformaticoApplication {

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(ProjetoInformaticoApplication.class, args);
    }

}
