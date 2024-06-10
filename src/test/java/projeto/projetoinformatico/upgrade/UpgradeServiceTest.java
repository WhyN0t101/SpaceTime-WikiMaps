package projeto.projetoinformatico.upgrade;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import projeto.projetoinformatico.dtos.RoleUpgradeDTO;
import projeto.projetoinformatico.model.roleUpgrade.RoleStatus;
import projeto.projetoinformatico.model.roleUpgrade.RoleUpgrade;
import projeto.projetoinformatico.model.roleUpgrade.RoleUpgradeRepository;
import projeto.projetoinformatico.model.users.Role;
import projeto.projetoinformatico.model.users.User;
import projeto.projetoinformatico.model.users.UserRepository;
import projeto.projetoinformatico.requests.StatusRequest;
import projeto.projetoinformatico.service.UpgradeService;
import projeto.projetoinformatico.utils.ModelMapperUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UpgradeServiceTest {

    private  RoleUpgradeRepository roleUpgradeRepository;
    @Mock
    private UserRepository userRepository;

    private UpgradeService upgradeService;
    @Mock
    private ModelMapperUtils mapperUtils;

    @BeforeEach
    public void setUp() {
        roleUpgradeRepository = mock(RoleUpgradeRepository.class);
        mapperUtils = mock(ModelMapperUtils.class);
        userRepository = mock(UserRepository.class);
        upgradeService = new UpgradeService(roleUpgradeRepository,userRepository,mapperUtils);
    }

    @Test
    public void requestUpgrade_ValidUsername_Success() {
        // Arrange
        String username = "testUser";
        String reason = "Testing upgrade";
        User user = new User();
        user.setUsername(username);
        user.setRole(Role.USER); // Ensure the role is set
        RoleUpgrade roleUpgrade = new RoleUpgrade();
        roleUpgrade.setUser(user);
        when(userRepository.findByUsername(username)).thenReturn(user);
        when(roleUpgradeRepository.findFirstByUserIdAndStatusInOrderByTimestampDesc(any(), any()))
                .thenReturn(Optional.empty());
        when(roleUpgradeRepository.findFirstByUserOrderByTimestampDesc(user)).thenReturn(Optional.empty());
        when(roleUpgradeRepository.save(any())).thenReturn(roleUpgrade);
        when(mapperUtils.roleUpgradeToDTO(any(), any())).thenReturn(new RoleUpgradeDTO());

        // Act
        RoleUpgradeDTO result = upgradeService.requestUpgrade(username, reason);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUserDTO().getUsername());
        verify(userRepository, times(1)).findByUsername(username);
        verify(roleUpgradeRepository, times(1)).findFirstByUserIdAndStatusInOrderByTimestampDesc(any(), any());
        verify(roleUpgradeRepository, times(1)).findFirstByUserOrderByTimestampDesc(user);
        verify(roleUpgradeRepository, times(1)).save(any());
        verify(mapperUtils, times(1)).roleUpgradeToDTO(any(), any());
    }
    @Test
    public void handleRequest_ValidRequest_Success() {
        // Arrange
        StatusRequest statusRequest = new StatusRequest();
        statusRequest.setStatus("ACCEPTED");
        statusRequest.setMessage("Upgrade request accepted");

        Long id = 1L;
        RoleUpgrade roleUpgrade = new RoleUpgrade();
        User user = new User();
        user.setUsername("testUser");
        roleUpgrade.setUser(user);

        when(roleUpgradeRepository.findById(id)).thenReturn(Optional.of(roleUpgrade));
        when(roleUpgradeRepository.save(any(RoleUpgrade.class))).thenReturn(roleUpgrade);
        when(mapperUtils.roleUpgradeToDTO(any(RoleUpgrade.class), eq(RoleUpgradeDTO.class)))
                .thenReturn(new RoleUpgradeDTO());

        // Act
        RoleUpgradeDTO result = upgradeService.handleRequest(statusRequest, id);

        // Assert
        assertNotNull(result);
        assertEquals("testUser", result.getUserDTO().getUsername());
        verify(roleUpgradeRepository, times(1)).findById(id);
        verify(roleUpgradeRepository, times(1)).save(any(RoleUpgrade.class));
        verify(mapperUtils, times(1)).roleUpgradeToDTO(any(RoleUpgrade.class), eq(RoleUpgradeDTO.class));
    }
    @Test
    public void getRequestsByNameAndStatusPaged_WithValidParameters_ReturnsPage() {
        // Arrange
        String username = "testUser";
        String status = "ACCEPTED";
        Pageable pageable = PageRequest.of(0, 10);
        List<RoleUpgrade> roleUpgrades = Collections.singletonList(new RoleUpgrade());
        Page<RoleUpgrade> roleUpgradePage = new PageImpl<>(roleUpgrades);
        when(roleUpgradeRepository.findByUserUsernameContainingIgnoreCaseAndStatus(username, RoleStatus.ACCEPTED, pageable)).thenReturn(roleUpgradePage);
        when(mapperUtils.roleUpgradeToDTO(any(RoleUpgrade.class), eq(RoleUpgradeDTO.class))).thenReturn(new RoleUpgradeDTO());

        // Act
        Page<RoleUpgradeDTO> result = upgradeService.getRequestsByNameAndStatusPaged(username, status, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(roleUpgradePage.getTotalElements(), result.getTotalElements());
        verify(roleUpgradeRepository, times(1)).findByUserUsernameContainingIgnoreCaseAndStatus(username, RoleStatus.ACCEPTED, pageable);
        verify(mapperUtils, times(roleUpgrades.size())).roleUpgradeToDTO(any(RoleUpgrade.class), eq(RoleUpgradeDTO.class));
    }

    @Test
    public void getByStatusPaged_WithValidParameters_ReturnsPage() {
        // Arrange
        String status = "PENDING";
        Pageable pageable = PageRequest.of(0, 10);
        List<RoleUpgrade> roleUpgrades = Collections.singletonList(new RoleUpgrade());
        Page<RoleUpgrade> roleUpgradePage = new PageImpl<>(roleUpgrades);
        when(roleUpgradeRepository.findByStatus(RoleStatus.PENDING, pageable)).thenReturn(roleUpgradePage);
        when(mapperUtils.roleUpgradeToDTO(any(RoleUpgrade.class), eq(RoleUpgradeDTO.class))).thenReturn(new RoleUpgradeDTO());

        // Act
        Page<RoleUpgradeDTO> result = upgradeService.getByStatusPaged(status, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(roleUpgradePage.getTotalElements(), result.getTotalElements());
        verify(roleUpgradeRepository, times(1)).findByStatus(RoleStatus.PENDING, pageable);
        verify(mapperUtils, times(roleUpgrades.size())).roleUpgradeToDTO(any(RoleUpgrade.class), eq(RoleUpgradeDTO.class));
    }


    @Test
    public void getRequestsContainingUsernamePaged_WithValidParameters_ReturnsPage() {
        // Arrange
        String username = "testUser";
        Pageable pageable = PageRequest.of(0, 10);
        List<RoleUpgrade> roleUpgrades = Collections.singletonList(new RoleUpgrade());
        Page<RoleUpgrade> roleUpgradePage = new PageImpl<>(roleUpgrades);
        when(roleUpgradeRepository.findByUserUsernameContainingIgnoreCase(username, pageable)).thenReturn(roleUpgradePage);
        when(mapperUtils.roleUpgradeToDTO(any(RoleUpgrade.class), eq(RoleUpgradeDTO.class))).thenReturn(new RoleUpgradeDTO());

        // Act
        Page<RoleUpgradeDTO> result = upgradeService.getRequestsContainingUsernamePaged(username, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(roleUpgradePage.getTotalElements(), result.getTotalElements());
        verify(roleUpgradeRepository, times(1)).findByUserUsernameContainingIgnoreCase(username, pageable);
        verify(mapperUtils, times(roleUpgrades.size())).roleUpgradeToDTO(any(RoleUpgrade.class), eq(RoleUpgradeDTO.class));
    }

    @Test
    public void getAllRequestsPaged_WithValidParameters_ReturnsPage() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<RoleUpgrade> roleUpgrades = Collections.singletonList(new RoleUpgrade());
        Page<RoleUpgrade> roleUpgradePage = new PageImpl<>(roleUpgrades);
        when(roleUpgradeRepository.findAll(pageable)).thenReturn(roleUpgradePage);
        when(mapperUtils.roleUpgradeToDTO(any(RoleUpgrade.class), eq(RoleUpgradeDTO.class))).thenReturn(new RoleUpgradeDTO());

        // Act
        Page<RoleUpgradeDTO> result = upgradeService.getAllRequestsPaged(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(roleUpgradePage.getTotalElements(), result.getTotalElements());
        verify(roleUpgradeRepository, times(1)).findAll(pageable);
        verify(mapperUtils, times(roleUpgrades.size())).roleUpgradeToDTO(any(RoleUpgrade.class), eq(RoleUpgradeDTO.class));
    }





}
