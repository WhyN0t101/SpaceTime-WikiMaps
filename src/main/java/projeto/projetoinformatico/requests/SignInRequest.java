package projeto.projetoinformatico.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class SignInRequest {
    @NotBlank(message = "Username cannot be blank")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    private String password;
}
