package projeto.projetoinformatico.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import projeto.projetoinformatico.constraints.Validators.ChangePasswordValidator;
import projeto.projetoinformatico.constraints.Validators.RoleStatusValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ChangePasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)

public @interface ChangePassword {
    String message() default "Invalid role status";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
