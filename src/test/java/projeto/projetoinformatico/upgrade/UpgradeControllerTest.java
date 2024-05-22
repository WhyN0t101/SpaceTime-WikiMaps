package projeto.projetoinformatico.upgrade;

import com.github.jsonldjava.shaded.com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import projeto.projetoinformatico.controllers.SearchController;
import projeto.projetoinformatico.controllers.UpgradeController;
import projeto.projetoinformatico.dtos.Paged.RoleUpgradePageDTO;
import projeto.projetoinformatico.dtos.RoleUpgradeDTO;
import projeto.projetoinformatico.exceptions.Exception.InvalidParamsRequestException;
import projeto.projetoinformatico.service.SearchService;
import projeto.projetoinformatico.service.UpgradeService;
import projeto.projetoinformatico.utils.Validation;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UpgradeControllerTest {

    private UpgradeService upgradeService;

    private UpgradeController upgradeController;

    @BeforeEach
    public void setUp() {
        upgradeService = mock(UpgradeService.class);
        upgradeController = new UpgradeController(upgradeService);
    }

    @Test
    public void testGetAllRequests_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<RoleUpgradeDTO> requestsPage = new PageImpl<>(Collections.emptyList());
        when(upgradeService.getAllRequestsPaged(pageable)).thenReturn(requestsPage);

        RoleUpgradePageDTO expectedResponse = new RoleUpgradePageDTO(
                requestsPage.getContent(),
                requestsPage.getNumber(),
                (int) requestsPage.getTotalElements(),
                requestsPage.getTotalPages()
        );

        ResponseEntity<RoleUpgradePageDTO> responseEntity = upgradeController.getAllRequests(null, null, 0, 10);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());
        verify(upgradeService).getAllRequestsPaged(pageable);
    }

    @Test
    public void testGetAllRequests_WithStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        String status = "PENDING";
        Page<RoleUpgradeDTO> requestsPage = new PageImpl<>(Collections.emptyList());
        when(upgradeService.getByStatusPaged(status, pageable)).thenReturn(requestsPage);

        RoleUpgradePageDTO expectedResponse = new RoleUpgradePageDTO(
                requestsPage.getContent(),
                requestsPage.getNumber(),
                (int) requestsPage.getTotalElements(),
                requestsPage.getTotalPages()
        );

        ResponseEntity<RoleUpgradePageDTO> responseEntity = upgradeController.getAllRequests(status, null, 0, 10);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());
        verify(upgradeService).getByStatusPaged(status, pageable);
    }

    @Test
    public void testGetAllRequests_WithUsername() {
        Pageable pageable = PageRequest.of(0, 10);
        String username = "testuser";
        Page<RoleUpgradeDTO> requestsPage = new PageImpl<>(Collections.emptyList());
        when(upgradeService.getRequestsContainingUsernamePaged(username, pageable)).thenReturn(requestsPage);

        RoleUpgradePageDTO expectedResponse = new RoleUpgradePageDTO(
                requestsPage.getContent(),
                requestsPage.getNumber(),
                (int) requestsPage.getTotalElements(),
                requestsPage.getTotalPages()
        );

        ResponseEntity<RoleUpgradePageDTO> responseEntity = upgradeController.getAllRequests(null, username, 0, 10);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());
        verify(upgradeService).getRequestsContainingUsernamePaged(username, pageable);
    }

    @Test
    public void testGetAllRequests_WithStatusAndUsername() {
        Pageable pageable = PageRequest.of(0, 10);
        String status = "PENDING";
        String username = "testuser";
        Page<RoleUpgradeDTO> requestsPage = new PageImpl<>(Collections.emptyList());
        when(upgradeService.getRequestsByNameAndStatusPaged(username, status, pageable)).thenReturn(requestsPage);

        RoleUpgradePageDTO expectedResponse = new RoleUpgradePageDTO(
                requestsPage.getContent(),
                requestsPage.getNumber(),
                (int) requestsPage.getTotalElements(),
                requestsPage.getTotalPages()
        );

        ResponseEntity<RoleUpgradePageDTO> responseEntity = upgradeController.getAllRequests(status, username, 0, 10);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());
        verify(upgradeService).getRequestsByNameAndStatusPaged(username, status, pageable);
    }

    @Test
    public void testGetAllRequests_InvalidSize() {
        InvalidParamsRequestException exception = assertThrows(
                InvalidParamsRequestException.class,
                () -> upgradeController.getAllRequests(null, null, 0, 0)
        );

        assertEquals("Invalid size of pagination", exception.getMessage());
        verify(upgradeService, never()).getAllRequestsPaged(any(Pageable.class));
    }

    @Test
    public void testGetAllRequests_InvalidPage() {
        InvalidParamsRequestException exception = assertThrows(
                InvalidParamsRequestException.class,
                () -> upgradeController.getAllRequests(null, null, -1, 10)
        );

        assertEquals("Invalid page of pagination", exception.getMessage());
        verify(upgradeService, never()).getAllRequestsPaged(any(Pageable.class));
    }
}
