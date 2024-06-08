package projeto.projetoinformatico.responses;

import org.junit.jupiter.api.Test;
import projeto.projetoinformatico.dtos.RoleUpgradeDTO;
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.model.roleUpgrade.RoleStatus;
import projeto.projetoinformatico.model.roleUpgrade.RoleUpgrade;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class AuthenticationResponseTest {

    @Test
    void testEqualsAndHashCode() {
        UserDTO user1 = new UserDTO();
        user1.setId(1L);
        user1.setUsername("testUser1");

        UserDTO user2 = new UserDTO();
        user2.setId(2L);
        user2.setUsername("testUser2");

        AuthenticationResponse response1 = new AuthenticationResponse();
        response1.setAccessToken("accessToken1");
        response1.setRefreshToken("refreshToken1");
        response1.setUser(user1);

        AuthenticationResponse response2 = new AuthenticationResponse();
        response2.setAccessToken("accessToken2");
        response2.setRefreshToken("refreshToken2");
        response2.setUser(user2);

        AuthenticationResponse response3 = new AuthenticationResponse();
        response3.setAccessToken("accessToken1");
        response3.setRefreshToken("refreshToken1");
        response3.setUser(user1);

        // Test for equals method
        assertEquals(response1, response1); // Reflexive
        assertEquals(response1, response3); // Symmetric
        assertEquals(response3, response1); // Symmetric
        assertNotEquals(response1, response2); // Different access token
        assertNotEquals(response1, null); // Null comparison
        assertNotEquals(response1, new Object()); // Different class

        // Test for hashCode method
        assertEquals(response1.hashCode(), response3.hashCode());
    }

    @Test
    void testToString() {
        UserDTO user = new UserDTO();
        user.setId(1L);
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setRole("ADMIN");
        user.setBlocked(false);
        RoleUpgrade roleUpgrade = new RoleUpgrade();

        roleUpgrade.setId(1L);
        roleUpgrade.setReason("Reason 1");
        user.setRoleUpgrade(roleUpgrade);
        AuthenticationResponse response = new AuthenticationResponse();
        response.setAccessToken("accessToken");
        response.setRefreshToken("refreshToken");
        response.setUser(user);

        String expected = "AuthenticationResponse(accessToken=accessToken, refreshToken=refreshToken, user=UserDTO(id=1, username=testUser, email=test@example.com, role=ADMIN, blocked=false, roleUpgrade=RoleUpgradeDTO(id=1, userDTO=null, reason=Reason 1, timestamp=null, status=null, message=null)))";
        assertEquals(expected, response.toString());
    }
}
