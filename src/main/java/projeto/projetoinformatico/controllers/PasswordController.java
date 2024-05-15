package projeto.projetoinformatico.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.exceptions.Exception.InvalidParamsRequestException;
import projeto.projetoinformatico.model.users.User;
import projeto.projetoinformatico.requests.UpdatePasswordRequest;
import projeto.projetoinformatico.service.PasswordService;
import projeto.projetoinformatico.service.UserService;

@RestController
@RequestMapping("/api")
public class PasswordController {

    private final UserService userService;
    private final PasswordService passwordService;


    public PasswordController(UserService userService, PasswordEncoder passwordEncoder, PasswordService passwordService) {
        this.userService = userService;
        this.passwordService = passwordService;
    }


    @PutMapping("/users/password")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<UserDTO> updatePassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest,
                                                  Authentication authentication) {
        String username = authentication.getName();
        UserDTO user = userService.getUserByUsername(username);
        if(!passwordService.validatePassword(user,updatePasswordRequest.getOldPassword())){
            throw new InvalidParamsRequestException("Old password does not match");
        }
        UserDTO updatedUser = passwordService.updatePassword(user, updatePasswordRequest.getNewPassword());
        return ResponseEntity.ok(updatedUser);
    }


}
