package projeto.projetoinformatico.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import projeto.projetoinformatico.dtos.LayerDTO;
import projeto.projetoinformatico.dtos.RoleUpgradeDTO;
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.model.layers.Layer;
import projeto.projetoinformatico.model.roleUpgrade.RoleUpgrade;
import projeto.projetoinformatico.model.users.User;

public class ModelMapperUtilsTest {

    private ModelMapperUtils modelMapperUtils;
    private ModelMapper modelMapperMock;

    @BeforeEach
    public void setUp() {
        modelMapperMock = mock(ModelMapper.class);
        modelMapperUtils = new ModelMapperUtils(modelMapperMock);
    }

    @Test
    public void testMap_ObjectAndDestinationType_ReturnsMappedObject() {
        // Given
        Object source = new Object();
        Object destination = new Object();

        when(modelMapperMock.map(source, Object.class)).thenReturn(destination);

        // When
        Object result = modelMapperUtils.map(source, Object.class);

        // Then
        assertEquals(destination, result);
    }

    @Test
    public void testUserToDTO_UserAndDestinationType_ReturnsUserDTO() {
        // Given
        User user = new User();
        UserDTO userDTO = new UserDTO();

        when(modelMapperMock.map(user, UserDTO.class)).thenReturn(userDTO);

        // When
        UserDTO result = modelMapperUtils.userToDTO(user, UserDTO.class);

        // Then
        assertEquals(userDTO, result);
    }

    @Test
    public void testLayerToDTO_LayerAndDestinationType_ReturnsLayerDTO() {
        // Given
        Layer layer = new Layer();
        LayerDTO layerDTO = new LayerDTO();

        when(modelMapperMock.map(layer, LayerDTO.class)).thenReturn(layerDTO);

        // When
        LayerDTO result = modelMapperUtils.layerToDTO(layer, LayerDTO.class);

        // Then
        assertEquals(layerDTO, result);
    }

    @Test
    public void testRoleUpgradeToDTO_RoleUpgradeAndDestinationType_ReturnsRoleUpgradeDTO() {
        // Given
        RoleUpgrade upgrade = new RoleUpgrade();
        RoleUpgradeDTO upgradeDTO = new RoleUpgradeDTO();

        when(modelMapperMock.map(upgrade, RoleUpgradeDTO.class)).thenReturn(upgradeDTO);

        // When
        RoleUpgradeDTO result = modelMapperUtils.roleUpgradeToDTO(upgrade, RoleUpgradeDTO.class);

        // Then
        assertEquals(upgradeDTO, result);
    }

    @Test
    public void testDtoToUser_UserDTO_ReturnsUser() {
        // Given
        UserDTO userDTO = new UserDTO();
        User user = new User();

        when(modelMapperMock.map(userDTO, User.class)).thenReturn(user);

        // When
        User result = modelMapperUtils.dtoToUser(userDTO);

        // Then
        assertEquals(user, result);
    }

    @Test
    public void testDtoToLayer_LayerDTO_ReturnsLayer() {
        // Given
        LayerDTO layerDTO = new LayerDTO();
        Layer layer = new Layer();

        when(modelMapperMock.map(layerDTO, Layer.class)).thenReturn(layer);

        // When
        Layer result = modelMapperUtils.dtoToLayer(layerDTO);

        // Then
        assertEquals(layer, result);
    }

    @Test
    public void testDtoToUpgrade_RoleUpgradeDTO_ReturnsRoleUpgrade() {
        // Given
        RoleUpgradeDTO upgradeDTO = new RoleUpgradeDTO();
        RoleUpgrade upgrade = new RoleUpgrade();

        when(modelMapperMock.map(upgradeDTO, RoleUpgrade.class)).thenReturn(upgrade);

        // When
        RoleUpgrade result = modelMapperUtils.dtoToUpgrade(upgradeDTO);

        // Then
        assertEquals(upgrade, result);
    }

    @Test
    public void testMap_ObjectAndClass_ReturnsMappedObject() {
        // Given
        Object source = new Object();
        Object destination = new Object();

        when(modelMapperMock.map(source, Object.class)).thenReturn(destination);

        // When
        Object result = modelMapperUtils.map(source, Object.class);

        // Then
        assertEquals(destination, result);
    }
}
