package projeto.projetoinformatico.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import projeto.projetoinformatico.service.UserService;

@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Welcome Admin");
    }

    @PutMapping("/users/{id}/block")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> blockUser(@PathVariable Long id) {
        userService.blockUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{id}/unblock")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> unlockUser(@PathVariable Long id) {
        userService.unblockUser(id);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
