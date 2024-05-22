package projeto.projetoinformatico.upgrade;

import com.github.jsonldjava.shaded.com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import projeto.projetoinformatico.controllers.SearchController;
import projeto.projetoinformatico.controllers.UpgradeController;
import projeto.projetoinformatico.dtos.Paged.RoleUpgradePageDTO;
import projeto.projetoinformatico.dtos.RoleUpgradeDTO;
import projeto.projetoinformatico.exceptions.Exception.InvalidParamsRequestException;
import projeto.projetoinformatico.exceptions.Exception.InvalidRequestException;
import projeto.projetoinformatico.exceptions.Exception.NotFoundException;
import projeto.projetoinformatico.requests.StatusRequest;
import projeto.projetoinformatico.requests.UpgradeRequest;
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
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @BeforeEach
    public void setUp() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("testuser", null);
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
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

    @Test
    public void testRequestUpgrade_Success() {
        String username = "testuser";
        String message = "Please upgrade my role.";
        UpgradeRequest upgradeRequest = new UpgradeRequest();
        upgradeRequest.setMessage(message);

        RoleUpgradeDTO roleUpgradeDTO = new RoleUpgradeDTO();
        when(upgradeService.requestUpgrade(username, message)).thenReturn(roleUpgradeDTO);

        ResponseEntity<RoleUpgradeDTO> responseEntity = upgradeController.requestUpgrade(upgradeRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(roleUpgradeDTO, responseEntity.getBody());
        verify(upgradeService).requestUpgrade(username, message);
    }

    @Test
    public void testRequestUpgrade_UserNotFound() {
        String username = "testuser";
        String message = "Please upgrade my role.";
        UpgradeRequest upgradeRequest = new UpgradeRequest();
        upgradeRequest.setMessage(message);

        when(upgradeService.requestUpgrade(username, message)).thenThrow(new NotFoundException("User not found"));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            upgradeController.requestUpgrade(upgradeRequest);
        });

        assertEquals("User not found", exception.getMessage());
        verify(upgradeService).requestUpgrade(username, message);
    }

    @Test
    public void testRequestUpgrade_InvalidRequest() {
        String username = "testuser";
        String message = "Please upgrade my role.";
        UpgradeRequest upgradeRequest = new UpgradeRequest();
        upgradeRequest.setMessage(message);

        when(upgradeService.requestUpgrade(username, message)).thenThrow(new InvalidRequestException("A request is still pending or has already been accepted."));

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            upgradeController.requestUpgrade(upgradeRequest);
        });

        assertEquals("A request is still pending or has already been accepted.", exception.getMessage());
        verify(upgradeService).requestUpgrade(username, message);
    }

    @Test
    public void testProcessUpgradeRequest_Success() {
        Long requestId = 1L;
        StatusRequest statusRequest = new StatusRequest();
        statusRequest.setStatus("ACCEPTED");
        statusRequest.setMessage("Request approved.");

        RoleUpgradeDTO roleUpgradeDTO = new RoleUpgradeDTO();
        when(upgradeService.handleRequest(statusRequest, requestId)).thenReturn(roleUpgradeDTO);

        ResponseEntity<RoleUpgradeDTO> responseEntity = upgradeController.processUpgradeRequest(requestId, statusRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(roleUpgradeDTO, responseEntity.getBody());
        verify(upgradeService).handleRequest(statusRequest, requestId);
    }

    @Test
    public void testProcessUpgradeRequest_RequestNotFound() {
        Long requestId = 1L;
        StatusRequest statusRequest = new StatusRequest();
        statusRequest.setStatus("ACCEPTED");
        statusRequest.setMessage("Request approved.");

        when(upgradeService.handleRequest(statusRequest, requestId)).thenThrow(new NotFoundException("Upgrade request not found"));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            upgradeController.processUpgradeRequest(requestId, statusRequest);
        });

        assertEquals("Upgrade request not found", exception.getMessage());
        verify(upgradeService).handleRequest(statusRequest, requestId);
    }

    @Test
    public void testProcessUpgradeRequest_InvalidStatus() {
        Long requestId = 1L;
        StatusRequest statusRequest = new StatusRequest();
        statusRequest.setStatus("INVALID_STATUS");
        statusRequest.setMessage("Invalid status request.");

        when(upgradeService.handleRequest(statusRequest, requestId)).thenThrow(new IllegalArgumentException("No enum constant"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            upgradeController.processUpgradeRequest(requestId, statusRequest);
        });

        assertEquals("No enum constant", exception.getMessage());
        verify(upgradeService).handleRequest(statusRequest, requestId);
    }

}
