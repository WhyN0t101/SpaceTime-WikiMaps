package projeto.projetoinformatico.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.exceptions.Exception.InvalidParamsRequestException;
import projeto.projetoinformatico.model.users.User;
import projeto.projetoinformatico.model.users.UserRepository;
import projeto.projetoinformatico.requests.UpdatePasswordRequest;
import projeto.projetoinformatico.service.PasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api")
public class PasswordController {

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public PasswordController(PasswordService passwordService, UserRepository userRepository) {
        this.passwordService = passwordService;
        this.userRepository = userRepository;
    }

    /**
     * Endpoint to update user password.
     *
     * @param updatePasswordRequest The request object containing old and new passwords.
     * @param authentication        The authentication object containing user details.
     * @return ResponseEntity with updated user DTO upon successful password update.
     * @throws InvalidParamsRequestException If the old password does not match the user's current password.
     */
    @Operation(summary = "Update user password", description = "Endpoint to update user password.")
    @PutMapping("/users/password")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserDTO> updatePassword(
            @Parameter(description = "Update password request details", required = true)
            @Valid @RequestBody UpdatePasswordRequest updatePasswordRequest,
            Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        if (!passwordService.validatePassword(user, updatePasswordRequest.getOldPassword())) {
            throw new InvalidParamsRequestException("Old password does not match");
        }
        UserDTO updatedUser = passwordService.updatePassword(user, updatePasswordRequest.getNewPassword());
        return ResponseEntity.ok(updatedUser);
    }

}
