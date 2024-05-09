package projeto.projetoinformatico.exceptions.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import projeto.projetoinformatico.dtos.RoleUpgradeDTO;
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

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/requests")
    public ResponseEntity<List<RoleUpgradeDTO>> getAllRequestsByStatus(@RequestParam(required = false) String status) {
        List<RoleUpgradeDTO> requests;
        if (status != null) {
            // Filter users by name and role
            requests = upgradeService.getByStatus(status);
        } else {
            // No filtering, return all users
            requests = upgradeService.getAllRequests();
        }
        return ResponseEntity.ok(requests);
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
