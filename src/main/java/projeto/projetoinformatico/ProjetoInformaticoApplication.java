package projeto.projetoinformatico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableCaching
@EntityScan("projeto.projetoinformatico.users")
public class ProjetoInformaticoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjetoInformaticoApplication.class, args);
    }

}
