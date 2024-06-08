package projeto.projetoinformatico.requests;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenRequestTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void testGetterAndSetter_token() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setToken("testToken");
        assertEquals("testToken", request.getToken());
    }

    @Test
    void testTokenNotBlank() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        Set<ConstraintViolation<RefreshTokenRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        ConstraintViolation<RefreshTokenRequest> violation = violations.iterator().next();
        assertEquals("Token cannot be blank", violation.getMessage());
    }
    @Test
    void testToString() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setToken("testToken");
        assertEquals("RefreshTokenRequest(token=testToken)", request.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        RefreshTokenRequest request1 = new RefreshTokenRequest();
        request1.setToken("token1");

        RefreshTokenRequest request2 = new RefreshTokenRequest();
        request2.setToken("token1");

        RefreshTokenRequest request3 = new RefreshTokenRequest();
        request3.setToken("token2");

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertEquals(request1.toString(), request2.toString());

        assertEquals(request1, request1);
        assertEquals(request1.hashCode(), request1.hashCode());
        assertEquals(request1.toString(), request1.toString());

        assertEquals(request2, request1);
        assertEquals(request2.hashCode(), request1.hashCode());
        assertEquals(request2.toString(), request1.toString());

        assertEquals(request1.equals(request3), request3.equals(request1));

        assertNotNull(request1);
    }
}
