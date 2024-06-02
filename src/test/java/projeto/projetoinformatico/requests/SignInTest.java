package projeto.projetoinformatico.requests;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SignInRequestTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void testGetterAndSetter_username() {
        SignInRequest request = new SignInRequest();
        request.setUsername("testUser");
        assertEquals("testUser", request.getUsername());
    }

    @Test
    void testGetterAndSetter_password() {
        SignInRequest request = new SignInRequest();
        request.setPassword("testPassword");
        assertEquals("testPassword", request.getPassword());
    }

    @Test
    void testUsernameNotBlank() {
        SignInRequest request = new SignInRequest();
        request.setPassword("Test");
        Set<ConstraintViolation<SignInRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        ConstraintViolation<SignInRequest> violation = violations.iterator().next();
        assertEquals("Username cannot be blank", violation.getMessage());
    }

    @Test
    void testPasswordNotBlank() {
        SignInRequest request = new SignInRequest();
        request.setUsername("testUser");
        Set<ConstraintViolation<SignInRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        ConstraintViolation<SignInRequest> violation = violations.iterator().next();
        assertEquals("Password cannot be blank", violation.getMessage());
    }
}
