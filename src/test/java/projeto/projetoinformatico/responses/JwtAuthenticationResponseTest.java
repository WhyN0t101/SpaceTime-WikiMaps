package projeto.projetoinformatico.responses;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtAuthenticationResponseTest {

    @Test
    void testEquals() {
        JwtAuthenticationResponse response1 = new JwtAuthenticationResponse();
        JwtAuthenticationResponse response2 = new JwtAuthenticationResponse();

        // Test reflexivity
        assertTrue(response1.equals(response1));

        // Test symmetricity
        assertTrue(response1.equals(response2));
        assertTrue(response2.equals(response1));

        // Test transitivity
        JwtAuthenticationResponse response3 = new JwtAuthenticationResponse();
        assertTrue(response2.equals(response3));
        assertTrue(response1.equals(response3));

        // Test null and different class comparison
        assertFalse(response1.equals(null));
        assertFalse(response1.equals("not a JwtAuthenticationResponse"));

        // Test inequality
        response2.setToken("token1");
        assertFalse(response1.equals(response2));
    }

    @Test
    void testHashCode() {
        JwtAuthenticationResponse response1 = new JwtAuthenticationResponse();
        JwtAuthenticationResponse response2 = new JwtAuthenticationResponse();

        assertEquals(response1.hashCode(), response2.hashCode());

        response1.setToken("token1");
        assertNotEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testToString() {
        JwtAuthenticationResponse response = new JwtAuthenticationResponse();
        assertNotNull(response.toString());
    }

    @Test
    void testAccessors() {
        JwtAuthenticationResponse response = new JwtAuthenticationResponse();
        response.setToken("token");
        response.setRefreshToken("refreshToken");

        assertEquals("token", response.getToken());
        assertEquals("refreshToken", response.getRefreshToken());
    }

    @Test
    void testCanEqual() {
        JwtAuthenticationResponse response1 = new JwtAuthenticationResponse();
        JwtAuthenticationResponse response2 = new JwtAuthenticationResponse();

        assertTrue(response1.canEqual(response2));
    }
}
