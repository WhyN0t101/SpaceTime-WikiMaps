package projeto.projetoinformatico.utils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import projeto.projetoinformatico.model.roleUpgrade.RoleStatus;

public class RoleStatusValidator implements ConstraintValidator<ValidRoleStatus, RoleStatus> {

    @Override
    public void initialize(ValidRoleStatus constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(RoleStatus status, ConstraintValidatorContext context) {
        if (status == null) {
            return false;
        }
        for (RoleStatus validStatus : RoleStatus.values()) {
            if (validStatus.equals(status)) {
                return true;
            }
        }
        return false;
    }

}
