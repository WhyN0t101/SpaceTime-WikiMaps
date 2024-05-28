package projeto.projetoinformatico.dtos;

import lombok.Data;
import projeto.projetoinformatico.model.roleUpgrade.RoleStatus;

import java.util.Date;

@Data
public class RoleUpgradeDTO {
    private Long id;
    private UserDTO user;
    private String reason;
    private Date timestamp;
    private RoleStatus status;
    private String message;

}