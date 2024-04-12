package projeto.projetoinformatico.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projeto.projetoinformatico.service.UserService;
import projeto.projetoinformatico.users.Role;
import projeto.projetoinformatico.users.User;

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
        // Additional validation if needed
        //System.out.println(user);
        User newUser = userService.createUser(user);
        return ResponseEntity.ok(newUser);
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
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }
}
