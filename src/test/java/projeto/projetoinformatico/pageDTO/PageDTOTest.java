package projeto.projetoinformatico.pageDTO;

import org.junit.jupiter.api.Test;
import projeto.projetoinformatico.dtos.Paged.LayerPageDTO;
import projeto.projetoinformatico.dtos.Paged.RoleUpgradePageDTO;
import projeto.projetoinformatico.dtos.Paged.UserPageDTO;
import projeto.projetoinformatico.dtos.LayerDTO;
import projeto.projetoinformatico.dtos.RoleUpgradeDTO;
import projeto.projetoinformatico.dtos.UserDTO;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PageDTOTest {

    @Test
    public void testEqualsAndHashCodeForLayerPageDTO() {
        List<LayerDTO> layers1 = new ArrayList<>();
        List<LayerDTO> layers2 = new ArrayList<>();
        layers1.add(new LayerDTO());
        layers2.add(new LayerDTO());

        LayerPageDTO layerPageDTO1 = new LayerPageDTO(layers1, 1, 10, 2);
        LayerPageDTO layerPageDTO2 = new LayerPageDTO(layers2, 1, 10, 2);

        assertEquals(layerPageDTO1, layerPageDTO2);
        assertEquals(layerPageDTO1.hashCode(), layerPageDTO2.hashCode());

        // Test inequality
        layerPageDTO2.setCurrentPage(2);
        assertNotEquals(layerPageDTO1, layerPageDTO2);
    }

    @Test
    public void testToStringForLayerPageDTO() {
        List<LayerDTO> layers = new ArrayList<>();
        layers.add(new LayerDTO());
        layers.add(new LayerDTO());

        LayerPageDTO layerPageDTO = new LayerPageDTO(layers, 1, 10, 2);
        String expected = "LayerPageDTO(layers=" + layers + ", currentPage=1, totalItems=10, totalPages=2)";
        assertEquals(expected, layerPageDTO.toString());
    }

    @Test
    public void testSetCurrentPageForLayerPageDTO() {
        LayerPageDTO layerPageDTO = new LayerPageDTO(new ArrayList<>(), 1, 10, 2);
        layerPageDTO.setCurrentPage(2);
        assertEquals(2, layerPageDTO.getCurrentPage());
    }

    @Test
    public void testSetTotalItemsForLayerPageDTO() {
        LayerPageDTO layerPageDTO = new LayerPageDTO(new ArrayList<>(), 1, 10, 2);
        layerPageDTO.setTotalItems(20);
        assertEquals(20, layerPageDTO.getTotalItems());
    }

    @Test
    public void testSetTotalPagesForLayerPageDTO() {
        LayerPageDTO layerPageDTO = new LayerPageDTO(new ArrayList<>(), 1, 10, 2);
        layerPageDTO.setTotalPages(3);
        assertEquals(3, layerPageDTO.getTotalPages());
    }

    @Test
    public void testCanEqualForLayerPageDTO() {
        LayerPageDTO layerPageDTO1 = new LayerPageDTO(new ArrayList<>(), 1, 10, 2);
        LayerPageDTO layerPageDTO2 = new LayerPageDTO(new ArrayList<>(), 1, 10, 2);
        assertEquals(layerPageDTO1, layerPageDTO2);
    }

    @Test
    public void testEqualsAndHashCodeUser() {
        List<UserDTO> users1 = new ArrayList<>();
        List<UserDTO> users2 = new ArrayList<>();
        users1.add(new UserDTO());
        users2.add(new UserDTO());

        UserPageDTO userPageDTO1 = new UserPageDTO(users1, 1, 10, 2);
        UserPageDTO userPageDTO2 = new UserPageDTO(users2, 1, 10, 2);

        assertEquals(userPageDTO1, userPageDTO2);
        assertEquals(userPageDTO1.hashCode(), userPageDTO2.hashCode());

        // Test inequality
        userPageDTO2.setCurrentPage(2);
        assertNotEquals(userPageDTO1, userPageDTO2);
    }

    @Test
    public void testToStringUser() {
        List<UserDTO> users = new ArrayList<>();
        users.add(new UserDTO());
        users.add(new UserDTO());

        UserPageDTO userPageDTO = new UserPageDTO(users, 1, 10, 2);
        String expected = "UserPageDTO(users=" + users + ", currentPage=1, totalItems=10, totalPages=2)";
        assertEquals(expected, userPageDTO.toString());
    }

    @Test
    public void testSetCurrentPageUser() {
        UserPageDTO userPageDTO = new UserPageDTO(new ArrayList<>(), 1, 10, 2);
        userPageDTO.setCurrentPage(2);
        assertEquals(2, userPageDTO.getCurrentPage());
    }

    @Test
    public void testSetTotalItemsUser() {
        UserPageDTO userPageDTO = new UserPageDTO(new ArrayList<>(), 1, 10, 2);
        userPageDTO.setTotalItems(20);
        assertEquals(20, userPageDTO.getTotalItems());
    }

    @Test
    public void testSetTotalPagesUser() {
        UserPageDTO userPageDTO = new UserPageDTO(new ArrayList<>(), 1, 10, 2);
        userPageDTO.setTotalPages(3);
        assertEquals(3, userPageDTO.getTotalPages());
    }

    @Test
    public void testCanEqualUser() {
        UserPageDTO userPageDTO1 = new UserPageDTO(new ArrayList<>(), 1, 10, 2);
        UserPageDTO userPageDTO2 = new UserPageDTO(new ArrayList<>(), 1, 10, 2);
        assertEquals(userPageDTO1, userPageDTO2);
    }
    @Test
    public void testEqualsAndHashCodeRoleUpgrade() {
        List<RoleUpgradeDTO> requests1 = new ArrayList<>();
        List<RoleUpgradeDTO> requests2 = new ArrayList<>();
        requests1.add(new RoleUpgradeDTO());
        requests2.add(new RoleUpgradeDTO());

        RoleUpgradePageDTO roleUpgradePageDTO1 = new RoleUpgradePageDTO(requests1, 1, 10, 2);
        RoleUpgradePageDTO roleUpgradePageDTO2 = new RoleUpgradePageDTO(requests2, 1, 10, 2);

        assertEquals(roleUpgradePageDTO1, roleUpgradePageDTO2);
        assertEquals(roleUpgradePageDTO1.hashCode(), roleUpgradePageDTO2.hashCode());

        // Test inequality
        roleUpgradePageDTO2.setCurrentPage(2);
        assertNotEquals(roleUpgradePageDTO1, roleUpgradePageDTO2);
    }

    @Test
    public void testToStringRoleUpgrade() {
        List<RoleUpgradeDTO> requests = new ArrayList<>();
        requests.add(new RoleUpgradeDTO());
        requests.add(new RoleUpgradeDTO());

        RoleUpgradePageDTO roleUpgradePageDTO = new RoleUpgradePageDTO(requests, 1, 10, 2);
        String expected = "RoleUpgradePageDTO(requests=" + requests + ", currentPage=1, totalItems=10, totalPages=2)";
        assertEquals(expected, roleUpgradePageDTO.toString());
    }

    @Test
    public void testSetCurrentPageRoleUpgrade() {
        RoleUpgradePageDTO roleUpgradePageDTO = new RoleUpgradePageDTO(new ArrayList<>(), 1, 10, 2);
        roleUpgradePageDTO.setCurrentPage(2);
        assertEquals(2, roleUpgradePageDTO.getCurrentPage());
    }

    @Test
    public void testSetTotalItemsRoleUpgrade() {
        RoleUpgradePageDTO roleUpgradePageDTO = new RoleUpgradePageDTO(new ArrayList<>(), 1, 10, 2);
        roleUpgradePageDTO.setTotalItems(20);
        assertEquals(20, roleUpgradePageDTO.getTotalItems());
    }

    @Test
    public void testSetTotalPagesRoleUpgrade() {
        RoleUpgradePageDTO roleUpgradePageDTO = new RoleUpgradePageDTO(new ArrayList<>(), 1, 10, 2);
        roleUpgradePageDTO.setTotalPages(3);
        assertEquals(3, roleUpgradePageDTO.getTotalPages());
    }

    @Test
    public void testCanEqualRoleUpgrade() {
        RoleUpgradePageDTO roleUpgradePageDTO1 = new RoleUpgradePageDTO(new ArrayList<>(), 1, 10, 2);
        RoleUpgradePageDTO roleUpgradePageDTO2 = new RoleUpgradePageDTO(new ArrayList<>(), 1, 10, 2);
        assertEquals(roleUpgradePageDTO1, roleUpgradePageDTO2);
    }
}
