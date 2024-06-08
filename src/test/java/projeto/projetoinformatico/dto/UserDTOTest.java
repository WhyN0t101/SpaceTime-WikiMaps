package projeto.projetoinformatico.dto;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import projeto.projetoinformatico.dtos.RoleUpgradeDTO;
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.model.roleUpgrade.RoleUpgrade;
import projeto.projetoinformatico.model.roleUpgrade.RoleStatus;
import projeto.projetoinformatico.model.users.Role;
import projeto.projetoinformatico.model.users.User;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class UserDTOTest {

    @Test
    public void testSetRoleUpgrade() {
        RoleUpgrade roleUpgrade = Mockito.mock(RoleUpgrade.class);
        User user = Mockito.mock(User.class);

        Mockito.when(roleUpgrade.getId()).thenReturn(1L);
        Mockito.when(roleUpgrade.getUser()).thenReturn(user);
        Mockito.when(roleUpgrade.getReason()).thenReturn("Promotion");
        Mockito.when(roleUpgrade.getTimestamp()).thenReturn(new Date());
        Mockito.when(roleUpgrade.getStatus()).thenReturn(RoleStatus.ACCEPTED);
        Mockito.when(roleUpgrade.getMessage()).thenReturn("Approved by admin");

        Mockito.when(user.getId()).thenReturn(1L);
        Mockito.when(user.getUsername()).thenReturn("testuser");
        Mockito.when(user.getEmail()).thenReturn("testuser@example.com");
        Mockito.when(user.getRole()).thenReturn(Role.ADMIN);

        UserDTO userDTO = new UserDTO();
        userDTO.setRoleUpgrade(roleUpgrade);

        RoleUpgradeDTO roleUpgradeDTO = userDTO.getRoleUpgrade();
        assertNotNull(roleUpgradeDTO);
        assertEquals(1L, roleUpgradeDTO.getId());
        assertNotNull(roleUpgradeDTO.getUserDTO());
        assertEquals(1L, roleUpgradeDTO.getUserDTO().getId());
        assertEquals("testuser", roleUpgradeDTO.getUserDTO().getUsername());
        assertEquals("testuser@example.com", roleUpgradeDTO.getUserDTO().getEmail());
        assertEquals("ADMIN", roleUpgradeDTO.getUserDTO().getRole());
        assertEquals("Promotion", roleUpgradeDTO.getReason());
        assertEquals(roleUpgrade.getTimestamp(), roleUpgradeDTO.getTimestamp());
        assertEquals(RoleStatus.ACCEPTED, roleUpgradeDTO.getStatus());
        assertEquals("Approved by admin", roleUpgradeDTO.getMessage());
    }


    @Test
    public void testSetBlocked() {
        UserDTO userDTO = new UserDTO();
        userDTO.setBlocked(true);

        assertTrue(userDTO.isBlocked());
    }

    @Test
    public void testSetUsername() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");

        assertEquals("testuser", userDTO.getUsername());
    }

    @Test
    public void testSetEmail() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("testuser@example.com");

        assertEquals("testuser@example.com", userDTO.getEmail());
    }

    @Test
    public void testSetRole() {
        UserDTO userDTO = new UserDTO();
        userDTO.setRole("ADMIN");

        assertEquals("ADMIN", userDTO.getRole());
    }

    @Test
    public void testEqualsAndHashCode() {
        UserDTO userDTO1 = new UserDTO();
        userDTO1.setId(1L);
        userDTO1.setUsername("testuser");
        userDTO1.setEmail("testuser@example.com");
        userDTO1.setRole("ADMIN");
        userDTO1.setBlocked(false);

        UserDTO userDTO2 = new UserDTO();
        userDTO2.setId(1L);
        userDTO2.setUsername("testuser");
        userDTO2.setEmail("testuser@example.com");
        userDTO2.setRole("ADMIN");
        userDTO2.setBlocked(false);

        UserDTO userDTO3 = new UserDTO();
        userDTO3.setId(1L);
        userDTO3.setUsername("testuser");
        userDTO3.setEmail("testuser@example.com");
        userDTO3.setRole("ADMIN");
        userDTO3.setBlocked(false);
        RoleUpgrade roleUpgrade = new RoleUpgrade();
        userDTO3.setRoleUpgrade(roleUpgrade);

        assertEquals(userDTO1, userDTO2);
        assertEquals(userDTO1.hashCode(), userDTO2.hashCode());
        assertNotEquals(userDTO1, userDTO3);  // Test branch where roleUpgrade is not null
    }

    @Test
    public void testToString() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testuser");
        userDTO.setEmail("testuser@example.com");
        userDTO.setRole("ADMIN");
        userDTO.setBlocked(false);

        String expected = "UserDTO(id=1, username=testuser, email=testuser@example.com, role=ADMIN, blocked=false, roleUpgrade=null)";
        assertEquals(expected, userDTO.toString());
    }
}
