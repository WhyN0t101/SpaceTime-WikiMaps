package projeto.projetoinformatico.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import projeto.projetoinformatico.dtos.LayerDTO;
import projeto.projetoinformatico.dtos.Paged.LayerPageDTO;
import projeto.projetoinformatico.dtos.Paged.UserPageDTO;
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.exceptions.Exception.InvalidParamsRequestException;
import projeto.projetoinformatico.requests.AlterRequest;
import projeto.projetoinformatico.responses.AuthenticationResponse;
import projeto.projetoinformatico.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint to retrieve a user by username.
     *
     * @param username The username of the user to retrieve.
     * @return ResponseEntity with the requested UserDTO.
     */
    @Operation(summary = "Get user by username", description = "Endpoint to retrieve a user by username.")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping("/users/{username}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of user"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserDTO> getUserByUsername(
            @Parameter(description = "Username of the user to retrieve", required = true)
            @PathVariable String username) {
        UserDTO user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    /**
     * Endpoint to retrieve all users with optional filters.
     *
     * @param name  Optional name filter.
     * @param role  Optional role filter.
     * @param page  Page number (default 0).
     * @param size  Page size (default 10).
     * @return ResponseEntity with paginated list of users.
     */
    @Operation(summary = "Get all users", description = "Endpoint to retrieve all users with optional filters.")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping("/users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of users"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<UserPageDTO> getAllUsers(
            @Parameter(description = "Name filter")
            @RequestParam(required = false) String name,
            @Parameter(description = "Role filter")
            @RequestParam(required = false) String role,
            @Parameter(description = "Page number")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size) {
        if (size < 1) {
            throw new InvalidParamsRequestException("Invalid size of pagination");
        }
        if (page < 0) {
            throw new InvalidParamsRequestException("Invalid page of pagination");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<UserDTO> users;
        if (name != null && role != null) {
            // Filter users by name and role
            users = userService.getUsersByNameAndRolePaged(name, role, pageable);
        } else if (name != null) {
            // Filter users by name only
            users = userService.getUserContainingUsernamePaged(name, pageable);
        } else if (role != null) {
            // Filter users by role only
            users = userService.getAllUsersByRolePaged(role, pageable);
        } else {
            // No filtering, return all users
            users = userService.getAllUsersPaged(pageable);
        }
        UserPageDTO response = new UserPageDTO(
                users.getContent(),
                users.getNumber(),
                (int) users.getTotalElements(),
                users.getTotalPages()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to retrieve all users by role.
     *
     * @param role The role to filter users.
     * @return ResponseEntity with a list of UserDTOs.
     */
    @Operation(summary = "Get all users by role", description = "Endpoint to retrieve all users by role.")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping("/users/role/{role}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of users by role"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Users not found")
    })
    public ResponseEntity<List<UserDTO>> getAllUsersByRole(
            @Parameter(description = "Role to filter users", required = true)
            @PathVariable String role) {
        List<UserDTO> users = userService.getAllUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    /**
     * Endpoint to retrieve a user by ID.
     *
     * @param id The ID of the user to retrieve.
     * @return ResponseEntity with the requested UserDTO.
     */
    @Operation(summary = "Get user by ID", description = "Endpoint to retrieve a user by ID.")
    @GetMapping("/users/id/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of user"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserDTO> getUserById(
            @Parameter(description = "ID of the user to retrieve", required = true)
            @PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Endpoint to retrieve layers of a user.
     *
     * @param page Page number (default 0).
     * @param size Page size (default 10).
     * @param id   The ID of the user.
     * @return ResponseEntity with paginated list of LayerDTOs.
     */
    @Operation(summary = "Get user layers", description = "Endpoint to retrieve layers of a user.")
    @GetMapping("/users/{id}/layers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of user layers"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<LayerPageDTO> getUserLayers(
            @Parameter(description = "Page number")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "ID of the user to retrieve layers", required = true)
            @PathVariable Long id) {
        if (size < 1) {
            throw new InvalidParamsRequestException("Invalid size of pagination");
        }
        if (page < 0) {
            throw new InvalidParamsRequestException("Invalid page of pagination");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<LayerDTO> layers = userService.getUserLayers(id, pageable);
        LayerPageDTO response = new LayerPageDTO(
                layers.getContent(),
                layers.getNumber(),
                (int) layers.getTotalElements(),
                layers.getTotalPages()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to retrieve the authenticated user.
     *
     * @return ResponseEntity with the authenticated UserDTO.
     */
    @Operation(summary = "Get authenticated user", description = "Endpoint to retrieve the authenticated user.")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping("/user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of authenticated user"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserDTO> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName();
        UserDTO user = userService.getUserByUsername(authenticatedUsername);
        return ResponseEntity.ok(user);
    }

    /**
     * Endpoint to update user role by ID.
     *
     * @param id   The ID of the user to update.
     * @param role The new role for the user.
     * @return ResponseEntity with the updated UserDTO.
     */
    @Operation(summary = "Update user role", description = "Endpoint to update user role by ID.")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/users/{id}/role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful update of user role"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserDTO> updateUserRole(
            @Parameter(description = "ID of the user to update role", required = true)
            @PathVariable Long id,
            @Parameter(description = "New role for the user", required = true)
            @RequestParam String role) {
        UserDTO updatedUser = userService.updateUserRole(id, role);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Endpoint to update user details.
     *
     * @param alterRequest     The request body containing username and/or email to update.
     * @param authentication   The authentication object for the current user.
     * @return ResponseEntity with the updated AuthenticationResponse.
     */
    @Operation(summary = "Update user details", description = "Endpoint to update user details.")
    @PutMapping("/user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful update of user details"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<AuthenticationResponse> updateUserDetails(
            @Parameter(description = "Request body containing username and/or email to update", required = true)
            @Valid @RequestBody AlterRequest alterRequest,
            @Parameter(description = "Authentication object for the current user", hidden = true)
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

    /**
     * Endpoint to delete the authenticated user.
     *
     * @return ResponseEntity with no content.
     */
    @Operation(summary = "Delete authenticated user", description = "Endpoint to delete the authenticated user.")
    @DeleteMapping("/user")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful deletion of authenticated user"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> deleteUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName();
        Long userId = userService.getUserByUsername(authenticatedUsername).getId();
        userService.deleteOwnUser(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint to say hello (for testing purposes).
     *
     * @return ResponseEntity with a greeting message.
     */
    @Operation(summary = "Say hello", description = "Endpoint to say hello (for testing purposes).")
    @GetMapping
    @PreAuthorize("hasAuthority('USER')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Welcome message"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Welcome User");
    }

}
