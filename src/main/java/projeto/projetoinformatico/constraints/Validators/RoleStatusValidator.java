package projeto.projetoinformatico.constraints.Validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import projeto.projetoinformatico.constraints.ValidRoleStatus;
import projeto.projetoinformatico.model.roleUpgrade.RoleStatus;

public class RoleStatusValidator implements ConstraintValidator<ValidRoleStatus, String> {

    @Override
    public void initialize(ValidRoleStatus constraintAnnotation) {
    }

    @Override
    public boolean isValid(String status, ConstraintValidatorContext context) {
        if (status == null || status.isBlank()) {
            return false; // Empty or null strings are considered invalid
        }
        // Check if the provided status string matches any of the enum values
        for (RoleStatus enumValue : RoleStatus.values()) {
            if (enumValue.name().equalsIgnoreCase(status)) {
                return true; // Match found, status is valid
            }
        }
        return false; // No matching enum value found, status is invalid
    }
}
