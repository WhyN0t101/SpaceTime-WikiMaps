package projeto.projetoinformatico.requests;

import jakarta.validation.constraints.NotBlank;

public class UpgradeRequest {
    @NotBlank(message = "Message cannot be blank")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
