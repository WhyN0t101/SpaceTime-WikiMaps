package projeto.projetoinformatico.requests;

import jakarta.validation.constraints.NotBlank;
import projeto.projetoinformatico.model.roleUpgrade.RoleStatus;
import projeto.projetoinformatico.utils.ValidRoleStatus;

public class StatusRequest {
    @ValidRoleStatus(message = "Status is invalid")
    private RoleStatus status;
    private String message;

    public RoleStatus getStatus() {
        return status;
    }

    public void setStatus(RoleStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
