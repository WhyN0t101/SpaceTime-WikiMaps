package projeto.projetoinformatico.controllers;


import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import projeto.projetoinformatico.dtos.LayerDTO;
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.model.layers.Layer;
import projeto.projetoinformatico.requests.AlterRequest;
import projeto.projetoinformatico.responses.AuthenticationResponse;
import projeto.projetoinformatico.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping("/users/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        UserDTO user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers(@RequestParam(required = false) String name,
                                                          @RequestParam(required = false) String role) {
        List<UserDTO> users;
        if (name != null && role != null) {
            // Filter users by name and role
            users = userService.getUsersByNameAndRole(name, role);
        } else if (name != null) {
            // Filter users by name only
            users = userService.getUserContainingUsername(name);
        } else if (role != null) {
            // Filter users by role only
            users = userService.getAllUsersByRole(role);
        } else {
            // No filtering, return all users
            users = userService.getAllUsers();
        }
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping("/users/role/{role}")
    public ResponseEntity<List<UserDTO>> getAllUsersByRole(@PathVariable String role) {
        List<UserDTO> users = userService.getAllUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping("/users/id/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users/{id}/layers")
    //@PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<List<LayerDTO>> getUserLayers(@PathVariable Long id) {
        List<LayerDTO> userLayers = userService.getUserLayers(id);
        return ResponseEntity.ok(userLayers);

    }
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping("/user")
    public ResponseEntity<UserDTO> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName();
        UserDTO user = userService.getUserByUsername(authenticatedUsername);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/users/{id}/role")
    public ResponseEntity<UserDTO> updateUserRole(@PathVariable Long id, @RequestParam String role) {
        UserDTO updatedUser = userService.updateUserRole(id, role);
        return ResponseEntity.ok(updatedUser);
    }

    //@PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/user")
    public ResponseEntity<AuthenticationResponse> updateUserRole(@Valid @RequestBody AlterRequest alterRequest,
                                                                 Authentication authentication) {
        String username = authentication.getName();
        AuthenticationResponse response = null;
        if (alterRequest.getUsername() != null && alterRequest.getEmail() != null) {
            response = userService.updateUserUsernameEmail(username, alterRequest.getUsername(), alterRequest.getEmail());
        } else if (alterRequest.getUsername() != null) {
            response = userService.updateUserUsername(username, alterRequest.getUsername());
        } else if (alterRequest.getEmail() != null) {
            response = userService.updateUserEmail(username, alterRequest.getEmail());
        }
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteLayer(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Welcome User");
    }

}
