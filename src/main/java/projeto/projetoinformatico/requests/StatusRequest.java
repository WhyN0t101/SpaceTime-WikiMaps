package projeto.projetoinformatico.requests;

import projeto.projetoinformatico.constraints.ValidRoleStatus;

public class StatusRequest {
    @ValidRoleStatus(message = "Status is invalid")
    private String status;
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
