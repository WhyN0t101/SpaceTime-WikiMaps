package projeto.projetoinformatico.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projeto.projetoinformatico.Exceptions.NotFoundException;
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

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User newUser = userService.createUser(user);
        return ResponseEntity.ok(newUser);
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new NotFoundException("User not found with username: " + username);
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
        if (user == null) {
            throw new NotFoundException("User not found with id: " + id);
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users/{id}/layers")
    public ResponseEntity<List<Layer>> getUserLayers(@PathVariable Long id) {
        String username = userService.getUsernameById(id);
        if (username == null) {
            throw new NotFoundException("User not found with id: " + id);
        }

        List<Layer> userLayers = userService.getUserLayers(username);
        if (userLayers.isEmpty()) {
            throw new NotFoundException("User layers not found for user with id: " + id);
        } else {
            return ResponseEntity.ok(userLayers);
        }
    }
    @GetMapping
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Welcome User");
    }
}
