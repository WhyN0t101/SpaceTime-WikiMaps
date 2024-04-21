package projeto.projetoinformatico.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import projeto.projetoinformatico.Exceptions.Exception.NotFoundException;
import projeto.projetoinformatico.model.layers.Layer;
import projeto.projetoinformatico.service.UserService;
import projeto.projetoinformatico.model.users.Role;
import projeto.projetoinformatico.model.users.User;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if(users.isEmpty()){
            throw new NotFoundException("No users with that role assigned");
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/role/{role}")
    public ResponseEntity<List<User>> getAllUsersByRole(@PathVariable Role role) {

        List<User> users = userService.getAllUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/id/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users/{id}/layers")
    public ResponseEntity<List<Layer>> getUserLayers(@PathVariable Long id) {
        String username = userService.getUsernameById(id);
        List<Layer> userLayers = userService.getUserLayers(username);
        return ResponseEntity.ok(userLayers);

    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Welcome User");
    }
}
