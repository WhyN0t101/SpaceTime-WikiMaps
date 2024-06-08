package projeto.projetoinformatico.models;

import org.junit.jupiter.api.Test;
import projeto.projetoinformatico.model.layers.Layer;
import projeto.projetoinformatico.model.users.Role;
import projeto.projetoinformatico.model.users.User;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class LayerTest {

    @Test
    void testGettersAndSetters() {
        // Create a sample user
        User user = new User();
        user.setId(1L);
        user.setUsername("john_doe");

        // Create a sample layer
        Layer layer = new Layer();
        layer.setId(1L);
        layer.setUser(user);
        layer.setLayerName("Test Layer");
        layer.setDescription("This is a test layer.");
        layer.setTimestamp(new Date());
        layer.setQuery("SELECT * FROM data");

        // Test getters
        assertEquals(1L, layer.getId());
        assertEquals(user, layer.getUser());
        assertEquals("Test Layer", layer.getLayerName());
        assertEquals("This is a test layer.", layer.getDescription());
        assertNotNull(layer.getTimestamp());
        assertEquals("SELECT * FROM data", layer.getQuery());

        // Test setters
        User newUser = new User();
        newUser.setId(2L);
        newUser.setUsername("jane_doe");

        layer.setId(2L);
        layer.setUser(newUser);
        layer.setLayerName("New Layer");
        layer.setDescription("This is a new layer.");
        Date newTimestamp = new Date();
        layer.setTimestamp(newTimestamp);
        layer.setQuery("SELECT * FROM newData");

        assertEquals(2L, layer.getId());
        assertEquals(newUser, layer.getUser());
        assertEquals("New Layer", layer.getLayerName());
        assertEquals("This is a new layer.", layer.getDescription());
        assertEquals(newTimestamp, layer.getTimestamp());
        assertEquals("SELECT * FROM newData", layer.getQuery());
    }

    @Test
    void testEqualsAndHashCode() {
        Layer layer1 = new Layer();
        layer1.setId(1L);
        Layer layer2 = new Layer();
        layer2.setId(1L);
        Layer layer3 = new Layer();
        layer3.setId(2L);

        assertEquals(layer1, layer2);
        assertNotEquals(layer1, layer3);

        assertEquals(layer1.hashCode(), layer2.hashCode());
        assertNotEquals(layer1.hashCode(), layer3.hashCode());
    }
    @Test
    void testToString() {
        // Create a sample user
        User user = new User();
        user.setId(1L);
        user.setUsername("john_doe");
        user.setPassword("password");
        user.setRole(Role.ADMIN);
        user.setEmail("john.doe@example.com");
        user.setAccountNonLocked(true);

        // Create a sample layer
        Layer layer = new Layer();
        layer.setId(1L);
        layer.setUser(user);
        layer.setLayerName("Test Layer");
        layer.setDescription("This is a test layer.");
        layer.setTimestamp(new Date()); // Set timestamp
        layer.setQuery("SELECT * FROM test_table"); // Set query

        // Expected toString result
        String expectedToString = "Layer(id=1, user=User(id=1, username=john_doe, password=password, role=ADMIN, email=john.doe@example.com, accountNonLocked=true), layerName=Test Layer, description=This is a test layer., timestamp=" + layer.getTimestamp() + ", query=" + layer.getQuery() + ")";

        // Ensure the toString method produces the expected output
        assertEquals(expectedToString, layer.toString());
    }
}
