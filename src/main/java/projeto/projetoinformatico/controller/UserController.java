package projeto.projetoinformatico.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projeto.projetoinformatico.service.UserService;
import projeto.projetoinformatico.users.User;

@RestController
@RequestMapping("/api") ///////////////<-------------
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

    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Welcome User");
    }
}
