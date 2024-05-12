package projeto.projetoinformatico.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import projeto.projetoinformatico.constraints.ChangePassword;

@Data
public class UpdatePasswordRequest {
    @NotBlank(message = "New Password cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String newPassword;

    @NotBlank(message = "Old Password cannot be blank")
    private String oldPassword;
}