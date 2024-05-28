package projeto.projetoinformatico.dtos;

import lombok.Data;
import projeto.projetoinformatico.model.roleUpgrade.RoleStatus;
import projeto.projetoinformatico.model.users.User;

import java.util.Date;

@Data
public class RoleUpgradeDTO {
    private Long id;
    private UserDTO userDTO;
    private String reason;
    private Date timestamp;
    private RoleStatus status;
    private String message;
    public void setUser(User user) {
        if (user != null) {
            this.userDTO = new UserDTO();
            this.userDTO.setId(user.getId());
            this.userDTO.setEmail(user.getEmail());
            this.userDTO.setUsername(user.getUsername());
            this.userDTO.setRole(user.getRole().toString());
        }
    }
}