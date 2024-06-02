package projeto.projetoinformatico.models;

import org.junit.jupiter.api.Test;
import projeto.projetoinformatico.model.roleUpgrade.RoleStatus;
import projeto.projetoinformatico.model.roleUpgrade.RoleUpgrade;
import projeto.projetoinformatico.model.users.User;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RoleUpgradeTest extends  RoleUpgrade{

    @Test
    void testEquals() {
        User user = new User();
        user.setId(1L);

        RoleUpgrade upgrade1 = new RoleUpgrade();
        upgrade1.setId(1L);
        upgrade1.setUser(user);
        upgrade1.setReason("Test Reason");
        upgrade1.setTimestamp(new Date());
        upgrade1.setStatus(RoleStatus.PENDING);
        upgrade1.setMessage("Test Message");

        RoleUpgrade upgrade2 = new RoleUpgrade();
        upgrade2.setId(1L);
        upgrade2.setUser(user);
        upgrade2.setReason("Test Reason");
        upgrade2.setTimestamp(new Date());
        upgrade2.setStatus(RoleStatus.PENDING);
        upgrade2.setMessage("Test Message");

        // Two role upgrades with the same attributes should be equal
        assertEquals(upgrade1, upgrade2);
    }

    @Test
    void testHashCode() {
        User user = new User();
        user.setId(1L);

        RoleUpgrade upgrade1 = new RoleUpgrade();
        upgrade1.setId(1L);
        upgrade1.setUser(user);
        upgrade1.setReason("Test Reason");
        upgrade1.setTimestamp(new Date());
        upgrade1.setStatus(RoleStatus.PENDING);
        upgrade1.setMessage("Test Message");

        RoleUpgrade upgrade2 = new RoleUpgrade();
        upgrade2.setId(1L);
        upgrade2.setUser(user);
        upgrade2.setReason("Test Reason");
        upgrade2.setTimestamp(new Date());
        upgrade2.setStatus(RoleStatus.PENDING);
        upgrade2.setMessage("Test Message");

        // Two role upgrades with the same attributes should have the same hash code
        assertEquals(upgrade1.hashCode(), upgrade2.hashCode());
    }

    @Test
    void testToString() {
        User user = new User();
        user.setId(1L);

        RoleUpgrade upgrade = new RoleUpgrade();
        upgrade.setId(1L);
        upgrade.setUser(user);
        upgrade.setReason("Test Reason");
        upgrade.setTimestamp(new Date());
        upgrade.setStatus(RoleStatus.PENDING);
        upgrade.setMessage("Test Message");

        // Ensure the toString method produces a non-null result
        assertNotNull(upgrade.toString());
    }

    @Test
    void testOnCreate() {
        // Create an instance of the subclass to access the protected method
        RoleUpgradeTest upgrade = new RoleUpgradeTest();
        upgrade.onCreate();

        // Ensure the onCreate method sets the timestamp and status correctly
        assertNotNull(upgrade.getTimestamp());
        assertEquals(RoleStatus.PENDING, upgrade.getStatus());
    }
}
