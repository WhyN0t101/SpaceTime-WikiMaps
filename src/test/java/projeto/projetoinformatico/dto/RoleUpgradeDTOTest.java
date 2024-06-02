package projeto.projetoinformatico.dto;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import projeto.projetoinformatico.dtos.RoleUpgradeDTO;
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.model.roleUpgrade.RoleStatus;
import projeto.projetoinformatico.model.users.User;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class RoleUpgradeDTOTest {

    @Test
    public void testSetUser() {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getId()).thenReturn(1L);
        Mockito.when(user.getUsername()).thenReturn("testuser");
        Mockito.when(user.getEmail()).thenReturn("testuser@example.com");
        Mockito.when(user.getRole()).thenReturn(projeto.projetoinformatico.model.users.Role.ADMIN);

        RoleUpgradeDTO roleUpgradeDTO = new RoleUpgradeDTO();
        roleUpgradeDTO.setUser(user);

        UserDTO userDTO = roleUpgradeDTO.getUserDTO();
        assertNotNull(userDTO);
        assertEquals(1L, userDTO.getId());
        assertEquals("testuser", userDTO.getUsername());
        assertEquals("testuser@example.com", userDTO.getEmail());
        assertEquals("ADMIN", userDTO.getRole());
    }

    @Test
    public void testSetReason() {
        RoleUpgradeDTO roleUpgradeDTO = new RoleUpgradeDTO();
        roleUpgradeDTO.setReason("Promotion");

        assertEquals("Promotion", roleUpgradeDTO.getReason());
    }

    @Test
    public void testSetTimestamp() {
        RoleUpgradeDTO roleUpgradeDTO = new RoleUpgradeDTO();
        Date now = new Date();
        roleUpgradeDTO.setTimestamp(now);

        assertEquals(now, roleUpgradeDTO.getTimestamp());
    }

    @Test
    public void testSetStatus() {
        RoleUpgradeDTO roleUpgradeDTO = new RoleUpgradeDTO();
        roleUpgradeDTO.setStatus(RoleStatus.ACCEPTED);

        assertEquals(RoleStatus.ACCEPTED, roleUpgradeDTO.getStatus());
    }

    @Test
    public void testSetMessage() {
        RoleUpgradeDTO roleUpgradeDTO = new RoleUpgradeDTO();
        roleUpgradeDTO.setMessage("Approved by admin");

        assertEquals("Approved by admin", roleUpgradeDTO.getMessage());
    }

    @Test
    public void testEqualsAndHashCode() {
        RoleUpgradeDTO roleUpgradeDTO1 = new RoleUpgradeDTO();
        roleUpgradeDTO1.setId(1L);
        roleUpgradeDTO1.setReason("Promotion");
        roleUpgradeDTO1.setTimestamp(new Date());
        roleUpgradeDTO1.setStatus(RoleStatus.ACCEPTED);
        roleUpgradeDTO1.setMessage("Approved by admin");

        RoleUpgradeDTO roleUpgradeDTO2 = new RoleUpgradeDTO();
        roleUpgradeDTO2.setId(1L);
        roleUpgradeDTO2.setReason("Promotion");
        roleUpgradeDTO2.setTimestamp(roleUpgradeDTO1.getTimestamp());
        roleUpgradeDTO2.setStatus(RoleStatus.ACCEPTED);
        roleUpgradeDTO2.setMessage("Approved by admin");

        RoleUpgradeDTO roleUpgradeDTO3 = new RoleUpgradeDTO();
        roleUpgradeDTO3.setId(1L);
        roleUpgradeDTO3.setReason("Promotion");
        roleUpgradeDTO3.setTimestamp(roleUpgradeDTO1.getTimestamp());
        roleUpgradeDTO3.setStatus(RoleStatus.ACCEPTED);
        roleUpgradeDTO3.setMessage("Approved by admin");
        UserDTO userDTO = new UserDTO();
        roleUpgradeDTO3.setUserDTO(userDTO);

        assertEquals(roleUpgradeDTO1, roleUpgradeDTO2);
        assertEquals(roleUpgradeDTO1.hashCode(), roleUpgradeDTO2.hashCode());
        assertNotEquals(roleUpgradeDTO1, roleUpgradeDTO3);  // Test branch where userDTO is not null
    }

    @Test
    public void testToString() {
        RoleUpgradeDTO roleUpgradeDTO = new RoleUpgradeDTO();
        roleUpgradeDTO.setId(1L);
        roleUpgradeDTO.setReason("Promotion");
        roleUpgradeDTO.setTimestamp(new Date());
        roleUpgradeDTO.setStatus(RoleStatus.ACCEPTED);
        roleUpgradeDTO.setMessage("Approved by admin");

        String expected = "RoleUpgradeDTO(id=1, userDTO=null, reason=Promotion, timestamp=" + roleUpgradeDTO.getTimestamp() + ", status=ACCEPTED, message=Approved by admin)";
        assertEquals(expected, roleUpgradeDTO.toString());
    }
}
