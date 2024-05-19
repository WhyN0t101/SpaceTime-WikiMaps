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
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.exceptions.Exception.InvalidParamsRequestException;
import projeto.projetoinformatico.requests.StatusRequest;
import projeto.projetoinformatico.requests.UpgradeRequest;
import projeto.projetoinformatico.service.UpgradeService;

import java.util.List;

@RestController
@RequestMapping("/api/upgrade")
public class UpgradeController {

    private final UpgradeService upgradeService;

    @Autowired
    public UpgradeController(UpgradeService upgradeService) {
        this.upgradeService = upgradeService;
    }

    @GetMapping("/requests")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RoleUpgradePageDTO> getAllRequests(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if(size < 1){
            throw new InvalidParamsRequestException("Invalid size of pagination");
        }
        if(page < 0){
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

    @PostMapping("/request")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<RoleUpgradeDTO> requestUpgrade(@Valid @RequestBody UpgradeRequest upgradeRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        RoleUpgradeDTO request =  upgradeService.requestUpgrade(username,upgradeRequest.getMessage());
        return ResponseEntity.ok(request);
    }
    @PutMapping("/request/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RoleUpgradeDTO> processUpgradeRequest(@PathVariable Long id, @Valid @RequestBody StatusRequest statusRequest) {
        RoleUpgradeDTO request = upgradeService.handleRequest(statusRequest, id);
        return ResponseEntity.ok(request);
    }
}
