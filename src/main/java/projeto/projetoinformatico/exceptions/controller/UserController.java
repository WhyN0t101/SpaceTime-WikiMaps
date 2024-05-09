package projeto.projetoinformatico.exceptions.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.exceptions.Exception.NotFoundException;
import projeto.projetoinformatico.model.layers.Layer;
import projeto.projetoinformatico.responses.UserResponse;
import projeto.projetoinformatico.service.UserService;
import projeto.projetoinformatico.model.users.Role;

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
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<List<Layer>> getUserLayers(@PathVariable Long id) {
        String username = userService.getUsernameById(id);
        List<Layer> userLayers = userService.getUserLayers(username);
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
    @PutMapping("/users/{username}/role")
    public ResponseEntity<UserDTO> updateUserRole(@PathVariable String username, @RequestParam String role) {
        UserDTO updatedUser = userService.updateUserRole(username, role);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Welcome User");
    }

}