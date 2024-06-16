package projeto.projetoinformatico.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import projeto.projetoinformatico.dtos.Paged.RoleUpgradePageDTO;
import projeto.projetoinformatico.dtos.RoleUpgradeDTO;
import projeto.projetoinformatico.exceptions.Exception.InvalidParamsRequestException;
import projeto.projetoinformatico.requests.StatusRequest;
import projeto.projetoinformatico.requests.UpgradeRequest;
import projeto.projetoinformatico.service.UpgradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/upgrade")
public class UpgradeController {

    private final UpgradeService upgradeService;

    @Autowired
    public UpgradeController(UpgradeService upgradeService) {
        this.upgradeService = upgradeService;
    }

    /**
     * Endpoint to retrieve all upgrade requests.
     *
     * @param status   Optional status filter.
     * @param username Optional username filter.
     * @param page     Page number (default 0).
     * @param size     Page size (default 10).
     * @return ResponseEntity with paginated list of upgrade requests.
     */
    @Operation(summary = "Get all upgrade requests", description = "Endpoint to retrieve all upgrade requests.")
    @GetMapping("/requests")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of upgrade requests"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<RoleUpgradePageDTO> getAllRequests(
            @Parameter(description = "Status filter")
            @RequestParam(required = false) String status,
            @Parameter(description = "Username filter")
            @RequestParam(required = false) String username,
            @Parameter(description = "Page number")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size) {
        if (size < 1) {
            throw new InvalidParamsRequestException("Invalid size of pagination");
        }
        if (page < 0) {
            throw new InvalidParamsRequestException("Invalid page of pagination");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<RoleUpgradeDTO> requestsPage;

        if (status != null && username != null) {
            requestsPage = upgradeService.getRequestsByNameAndStatusPaged(username, status, pageable);
        } else if (username != null) {
            requestsPage = upgradeService.getRequestsContainingUsernamePaged(username, pageable);
        } else if (status != null) {
            requestsPage = upgradeService.getByStatusPaged(status, pageable);
        } else {
            requestsPage = upgradeService.getAllRequestsPaged(pageable);
        }

        RoleUpgradePageDTO response = new RoleUpgradePageDTO(
                requestsPage.getContent(),
                requestsPage.getNumber(),
                (int) requestsPage.getTotalElements(),
                requestsPage.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to request an upgrade.
     *
     * @param upgradeRequest The upgrade request details.
     * @return ResponseEntity with the created upgrade request.
     */
    @Operation(summary = "Request upgrade", description = "Endpoint to request an upgrade.")
    @PostMapping("/request")
    @PreAuthorize("hasAuthority('USER')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful upgrade request"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<RoleUpgradeDTO> requestUpgrade(
            @Parameter(description = "Upgrade request details", required = true)
            @Valid @RequestBody UpgradeRequest upgradeRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        RoleUpgradeDTO request = upgradeService.requestUpgrade(username, upgradeRequest.getMessage());
        return ResponseEntity.ok(request);
    }

    /**
     * Endpoint to process an upgrade request.
     *
     * @param id            The ID of the upgrade request.
     * @param statusRequest The status update request.
     * @return ResponseEntity with the updated upgrade request.
     */
    @Operation(summary = "Process upgrade request", description = "Endpoint to process an upgrade request.")
    @PutMapping("/request/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful processing of upgrade request"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<RoleUpgradeDTO> processUpgradeRequest(
            @Parameter(description = "Upgrade request ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Status update request", required = true)
            @Valid @RequestBody StatusRequest statusRequest) {
        RoleUpgradeDTO request = upgradeService.handleRequest(statusRequest, id);
        return ResponseEntity.ok(request);
    }
}
