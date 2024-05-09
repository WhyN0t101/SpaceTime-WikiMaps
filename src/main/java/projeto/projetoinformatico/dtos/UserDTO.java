package projeto.projetoinformatico.dtos;

import lombok.Data;
import projeto.projetoinformatico.model.users.Role;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private Role role;

}
