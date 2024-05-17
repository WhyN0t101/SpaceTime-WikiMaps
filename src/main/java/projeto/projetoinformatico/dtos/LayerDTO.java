package projeto.projetoinformatico.dtos;

import lombok.Data;
import projeto.projetoinformatico.model.roleUpgrade.RoleUpgrade;
import projeto.projetoinformatico.model.users.User;

import java.util.Date;

@Data
public class LayerDTO {
    private Long id;
    private UserDTO userDTO;
    private String layerName;
    private String description;
    private Date timestamp;
    private String query;
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