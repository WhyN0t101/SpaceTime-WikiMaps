package projeto.projetoinformatico.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AlterRequest {

    private String username;

    @Email(message = "Invalid email format")
    private String email;
}
