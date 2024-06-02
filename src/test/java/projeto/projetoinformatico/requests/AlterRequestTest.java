package projeto.projetoinformatico.requests;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AlterRequestTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void testGetterAndSetter_username() {
        AlterRequest request = new AlterRequest();
        request.setUsername("testUser");
        assertEquals("testUser", request.getUsername());
    }

    @Test
    void testGetterAndSetter_email() {
        AlterRequest request = new AlterRequest();
        request.setEmail("test@example.com");
        assertEquals("test@example.com", request.getEmail());
    }

    @Test
    void testEmailValidation_ValidEmail() {
        AlterRequest request = new AlterRequest();
        request.setEmail("test@example.com");
        Set<ConstraintViolation<AlterRequest>> violations = validator.validate(request);
        assertEquals(0, violations.size());
    }

    @Test
    void testEmailValidation_InvalidEmail() {
        AlterRequest request = new AlterRequest();
        request.setEmail("invalid_email.com");
        Set<ConstraintViolation<AlterRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        ConstraintViolation<AlterRequest> violation = violations.iterator().next();
        assertEquals("Invalid email format", violation.getMessage());
    }

    @Test
    void testBlankUsername() {
        AlterRequest request = new AlterRequest();
        request.setUsername("");
        Set<ConstraintViolation<AlterRequest>> violations = validator.validate(request);
        assertEquals(0, violations.size());  // Empty strings are allowed
    }

    @Test
    void testNullUsername() {
        AlterRequest request = new AlterRequest();
        request.setUsername(null);
        Set<ConstraintViolation<AlterRequest>> violations = validator.validate(request);
        assertEquals(0, violations.size());  // Null values are allowed
    }
    @Test
    void testEqualsAndHashCode() {
        AlterRequest request1 = new AlterRequest();
        request1.setUsername("user1");
        request1.setEmail("user1@example.com");

        AlterRequest request2 = new AlterRequest();
        request2.setUsername("user1");
        request2.setEmail("user1@example.com");

        AlterRequest request3 = new AlterRequest();
        request3.setUsername("user2");
        request3.setEmail("user2@example.com");

        assertEquals(request1, request1);
        assertEquals(request1, request2);
        assertEquals(request2, request1);
        assertNotEquals(request2, request3);
        assertNotEquals(request1, request3);
        assertNotEquals(null, request1);
        assertNotEquals("user1", request1);
        assertNotEquals(request1, request3);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testToString() {
        AlterRequest request = new AlterRequest();
        request.setUsername("testUser");
        request.setEmail("test@example.com");

        String expectedToString = "AlterRequest(username=testUser, email=test@example.com)";
        assertEquals(expectedToString, request.toString());
    }
}
