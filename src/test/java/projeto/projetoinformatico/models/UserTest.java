package projeto.projetoinformatico.models;

import org.junit.jupiter.api.Test;
import projeto.projetoinformatico.model.users.Role;
import projeto.projetoinformatico.model.users.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UserTest {

    @Test
    void testEquals() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("john_doe");
        user1.setPassword("password");
        user1.setRole(Role.ADMIN);
        user1.setEmail("john.doe@example.com");
        user1.setAccountNonLocked(true);

        User user2 = new User();
        user2.setId(1L);
        user2.setUsername("john_doe");
        user2.setPassword("password");
        user2.setRole(Role.ADMIN);
        user2.setEmail("john.doe@example.com");
        user2.setAccountNonLocked(true);

        // Two users with the same attributes should be equal
        assertEquals(user1, user2);
    }

    @Test
    void testHashCode() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("john_doe");
        user1.setPassword("password");
        user1.setRole(Role.ADMIN);
        user1.setEmail("john.doe@example.com");
        user1.setAccountNonLocked(true);

        User user2 = new User();
        user2.setId(1L);
        user2.setUsername("john_doe");
        user2.setPassword("password");
        user2.setRole(Role.ADMIN);
        user2.setEmail("john.doe@example.com");
        user2.setAccountNonLocked(true);

        // Two users with the same attributes should have the same hash code
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testToString() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john_doe");
        user.setPassword("password");
        user.setRole(Role.ADMIN);
        user.setEmail("john.doe@example.com");
        user.setAccountNonLocked(true);

        // Expected toString result
        String expectedToString = "User(id=1, username=john_doe, password=password, role=ADMIN, email=john.doe@example.com, accountNonLocked=true)";

        // Ensure the toString method produces the expected output
        assertEquals(expectedToString, user.toString());
    }
}
