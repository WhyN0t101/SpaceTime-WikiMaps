package projeto.projetoinformatico.requests;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SignUpRequestTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void testGetterAndSetter_username() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("testUser");
        assertEquals("testUser", request.getUsername());
    }

    @Test
    void testGetterAndSetter_password() {
        SignUpRequest request = new SignUpRequest();
        request.setPassword("testPassword");
        assertEquals("testPassword", request.getPassword());
    }

    @Test
    void testGetterAndSetter_email() {
        SignUpRequest request = new SignUpRequest();
        request.setEmail("test@example.com");
        assertEquals("test@example.com", request.getEmail());
    }

    @Test
    void testUsernameNotBlank() {
        SignUpRequest request = new SignUpRequest();
        request.setPassword("Test111");
        request.setEmail("test@email.com");
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        ConstraintViolation<SignUpRequest> violation = violations.iterator().next();
        assertEquals("Username cannot be blank", violation.getMessage());
    }

    @Test
    void testPasswordNotBlank() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("testUser");
        request.setEmail("test@email.com");
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        ConstraintViolation<SignUpRequest> violation = violations.iterator().next();
        assertEquals("Password cannot be blank", violation.getMessage());
    }

    @Test
    void testPasswordLength() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("testUser");
        request.setPassword("short");
        request.setEmail("test@email.com");
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        ConstraintViolation<SignUpRequest> violation = violations.iterator().next();
        assertEquals("Password must be at least 6 characters long", violation.getMessage());
    }

    @Test
    void testEmailNotBlank() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("testUser");
        request.setPassword("testPassword");
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        ConstraintViolation<SignUpRequest> violation = violations.iterator().next();
        assertEquals("Email cannot be blank", violation.getMessage());
    }

    @Test
    void testInvalidEmailFormat() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("testUser");
        request.setPassword("testPassword");
        request.setEmail("invalidEmail");
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        ConstraintViolation<SignUpRequest> violation = violations.iterator().next();
        assertEquals("Invalid email format", violation.getMessage());
    }
    @Test
    void testEqualsAndHashCode() {
        SignUpRequest request1 = new SignUpRequest();
        request1.setUsername("user1");
        request1.setPassword("password1");
        request1.setEmail("user1@example.com");

        SignUpRequest request2 = new SignUpRequest();
        request2.setUsername("user1");
        request2.setPassword("password1");
        request2.setEmail("user1@example.com");

        SignUpRequest request3 = new SignUpRequest();
        request3.setUsername("user2");
        request3.setPassword("password2");
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
        SignUpRequest request = new SignUpRequest();
        request.setUsername("testUser");
        request.setPassword("testPassword");
        request.setEmail("test@example.com");

        String expectedToString = "SignUpRequest(username=testUser, password=testPassword, email=test@example.com)";
        assertEquals(expectedToString, request.toString());
    }
}
