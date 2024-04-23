package projeto.projetoinformatico.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {


    @NotBlank(message = "Token cannot be blank")
    private String token;
}
