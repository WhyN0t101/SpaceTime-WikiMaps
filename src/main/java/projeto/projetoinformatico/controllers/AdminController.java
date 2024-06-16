package projeto.projetoinformatico.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import projeto.projetoinformatico.service.UserService;

/**
 * Controller class for handling administrative operations.
 */
@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    /**
     * Endpoint to greet the admin.
     *
     * @return ResponseEntity with a greeting message.
     */
    @Operation(summary = "Greet the admin", description = "Endpoint to greet the admin.")
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Welcome Admin");
    }

    /**
     * Endpoint to block a user by ID.
     *
     * @param id The ID of the user to block.
     * @return ResponseEntity with no content upon successful user block.
     */
    @Operation(summary = "Block user by ID", description = "Endpoint to block a user by ID.")
    @PutMapping("/users/{id}/block")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> blockUser(
            @Parameter(description = "User ID to block", required = true)
            @PathVariable Long id) {
        userService.blockUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint to unlock a user by ID.
     *
     * @param id The ID of the user to unlock.
     * @return ResponseEntity with no content upon successful user unlock.
     */
    @Operation(summary = "Unlock user by ID", description = "Endpoint to unlock a user by ID.")
    @PutMapping("/users/{id}/unblock")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> unlockUser(
            @Parameter(description = "User ID to unblock", required = true)
            @PathVariable Long id) {
        userService.unblockUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint to delete a user by ID.
     *
     * @param id The ID of the user to be deleted.
     * @return ResponseEntity with no content upon successful deletion.
     */
    @Operation(summary = "Delete user by ID", description = "Endpoint to delete a user by ID.")
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteUserById(
            @Parameter(description = "User ID to delete", required = true)
            @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
