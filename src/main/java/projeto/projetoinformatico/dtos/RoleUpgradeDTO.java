package projeto.projetoinformatico.dtos;

import lombok.Data;
import projeto.projetoinformatico.model.roleUpgrade.RoleStatus;

import java.util.Date;

@Data
public class RoleUpgradeDTO {
    private Long id;
    private String username;
    private String reason;
    private Date timestamp;
    private RoleStatus status;
    private String message;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

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