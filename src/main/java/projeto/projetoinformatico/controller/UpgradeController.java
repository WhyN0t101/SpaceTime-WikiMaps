package projeto.projetoinformatico.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import projeto.projetoinformatico.model.roleUpgrade.RoleUpgrade;
import projeto.projetoinformatico.requests.StatusRequest;
import projeto.projetoinformatico.requests.UpgradeRequest;
import projeto.projetoinformatico.responses.UserResponse;
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
    public ResponseEntity<List<RoleUpgrade>> getAllRequestsByStatus(@RequestParam(required = false) String status) {

        List<RoleUpgrade> requests;
        if (status != null) {
            // Filter users by name and role
            requests = upgradeService.getByStatus(status);
        } else {
            // No filtering, return all users
            requests = upgradeService.getAllRequests();
        }

        return ResponseEntity.ok(requests);
    }
    // Endpoint for users to make an upgrade request
    @PostMapping("/request")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<RoleUpgrade> requestUpgrade(@Valid @RequestBody UpgradeRequest upgradeRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
       RoleUpgrade request =  upgradeService.requestUpgrade(username,upgradeRequest.getMessage());
        return ResponseEntity.ok(request);
    }

    // Endpoint for admin to accept/decline upgrade requests
    @PutMapping("/process-request/{requestId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RoleUpgrade> processUpgradeRequest(@Valid @RequestBody StatusRequest statusRequest, @PathVariable Long requestId) {
        RoleUpgrade request = upgradeService.handleRequest(statusRequest, requestId);
        return ResponseEntity.ok(request);
    }
}
