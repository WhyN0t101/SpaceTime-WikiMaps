package projeto.projetoinformatico.dtos;

import lombok.Data;
import projeto.projetoinformatico.model.roleUpgrade.RoleUpgrade;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private boolean blocked;
    private RoleUpgradeDTO roleUpgrade;

    public void setRoleUpgrade(RoleUpgrade roleUpgrade) {
        if (roleUpgrade != null) {
            this.roleUpgrade = new RoleUpgradeDTO();
            this.roleUpgrade.setId(roleUpgrade.getId());
            this.roleUpgrade.setUser(roleUpgrade.getUser());
            this.roleUpgrade.setReason(roleUpgrade.getReason());
            this.roleUpgrade.setTimestamp(roleUpgrade.getTimestamp());
            this.roleUpgrade.setStatus(roleUpgrade.getStatus());
            this.roleUpgrade.setMessage(roleUpgrade.getMessage());
        }
    }
}
