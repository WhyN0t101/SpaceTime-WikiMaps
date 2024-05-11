package projeto.projetoinformatico.responses;

import lombok.Data;
import projeto.projetoinformatico.dtos.UserDTO;

@Data
public class AuthenticationResponse {
    private String accessToken;
    private String refreshToken;
    private UserDTO user;

}
