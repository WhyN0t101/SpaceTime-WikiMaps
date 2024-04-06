package projeto.projetoinformatico.dto;

import lombok.Data;
import projeto.projetoinformatico.users.Role;
@Data
public class SignUpRequest {

    private String username;

    private String password;

    private String email;
}
