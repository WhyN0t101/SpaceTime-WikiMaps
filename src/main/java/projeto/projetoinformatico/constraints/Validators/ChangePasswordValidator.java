package projeto.projetoinformatico.constraints.Validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import projeto.projetoinformatico.constraints.ChangePassword;
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.requests.UpdatePasswordRequest;
import projeto.projetoinformatico.service.PasswordService;

public class ChangePasswordValidator implements ConstraintValidator<ChangePassword, UpdatePasswordRequest> {

    @Autowired
    private PasswordService passwordService;

    // Add a field to store the current user information
    private UserDTO currentUser;

    // Setter method to set the current user
    public void setCurrentUser(UserDTO currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public void initialize(ChangePassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(UpdatePasswordRequest updatePasswordRequest, ConstraintValidatorContext context) {
        String oldPassword = updatePasswordRequest.getOldPassword();
        if (oldPassword == null || oldPassword.isEmpty()) {
            return false; // Old password cannot be empty
        }

        // Validate old password using the current user's information
        return passwordService.validatePassword(currentUser, oldPassword);
    }
}
