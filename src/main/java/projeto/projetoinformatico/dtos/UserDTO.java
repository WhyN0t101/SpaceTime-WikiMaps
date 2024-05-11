package projeto.projetoinformatico.dtos;

import lombok.Data;
import projeto.projetoinformatico.model.roleUpgrade.RoleUpgrade;
import projeto.projetoinformatico.model.roleUpgrade.RoleStatus;
import projeto.projetoinformatico.model.users.User;

import java.util.Date;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String role; // Changed to String
    private RoleUpgradeDTO roleUpgrade;
    private String password; // Add password field

    public void setRoleUpgrade(RoleUpgrade roleUpgrade) {
        if (roleUpgrade != null) {
            this.roleUpgrade = new RoleUpgradeDTO();
            this.roleUpgrade.setId(roleUpgrade.getId());
            this.roleUpgrade.setUsername(roleUpgrade.getUsername());
            this.roleUpgrade.setReason(roleUpgrade.getReason());
            this.roleUpgrade.setTimestamp(roleUpgrade.getTimestamp());
            this.roleUpgrade.setStatus(roleUpgrade.getStatus());
            this.roleUpgrade.setMessage(roleUpgrade.getMessage());
        }
    }
}
