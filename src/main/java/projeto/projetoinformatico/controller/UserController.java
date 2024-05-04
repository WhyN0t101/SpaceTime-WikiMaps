package projeto.projetoinformatico.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import projeto.projetoinformatico.exceptions.Exception.NotFoundException;
import projeto.projetoinformatico.model.layers.Layer;
import projeto.projetoinformatico.responses.UserResponse;
import projeto.projetoinformatico.service.UserService;
import projeto.projetoinformatico.model.users.Role;

import java.util.ArrayList;
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
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        UserResponse user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers(@RequestParam(required = false) String name,
                                                          @RequestParam(required = false) String role) {

        // Check if the role parameter is provided and valid
        if (role != null) {
            try {
                Role roleEnum = Role.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new NotFoundException("Role not found: " + role);
            }
        }

        // If the role is valid, convert it to enum
        Role userRole = null;
        if (role != null) {
            userRole = Role.valueOf(role.toUpperCase());
        }

        List<UserResponse> users;

        if (name != null && userRole != null) {
            // Filter users by name and role
            users = userService.getUsersByNameAndRole(name, userRole);
        } else if (name != null) {
            // Filter users by name only
            users = userService.getUserContainingUsername(name);
        } else if (userRole != null) {
            // Filter users by role only
            users = userService.getAllUsersByRole(userRole);
        } else {
            // No filtering, return all users
            users = userService.getAllUsers();
        }

        return ResponseEntity.ok(users);
    }



    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping("/users/role/{role}")
    public ResponseEntity<List<UserResponse>> getAllUsersByRole(@PathVariable String role) {
        try {
            Role roleEnum = Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Role not found" + role);
        }
        List<UserResponse> users = userService.getAllUsersByRole(Role.valueOf(role.toUpperCase()));
        return ResponseEntity.ok(users);
    }



    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping("/users/id/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
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
    public ResponseEntity<UserResponse> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName();
        UserResponse user = userService.getUserByUsername(authenticatedUsername);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Welcome User");
    }
}
