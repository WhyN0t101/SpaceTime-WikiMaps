package projeto.projetoinformatico.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import projeto.projetoinformatico.constraints.Validators.RoleStatusValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RoleStatusValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRoleStatus {
    String message() default "Invalid role status";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
