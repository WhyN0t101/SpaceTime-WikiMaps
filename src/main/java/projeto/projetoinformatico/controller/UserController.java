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

        List<UserResponse> users;
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
    public ResponseEntity<List<UserResponse>> getAllUsersByRole(@PathVariable String role) {
        List<UserResponse> users = userService.getAllUsersByRole(role);
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

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/users/{username}/role")
    public ResponseEntity<UserResponse> updateUserRole(@PathVariable String username, @RequestParam String role) {
        UserResponse updatedUser = userService.updateUserRole(username, role);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Welcome User");
    }
}
