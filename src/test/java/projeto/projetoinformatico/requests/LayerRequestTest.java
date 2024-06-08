package projeto.projetoinformatico.requests;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LayerRequestTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void testGetterAndSetter_name() {
        LayerRequest request = new LayerRequest();
        request.setName("Test Layer");
        assertEquals("Test Layer", request.getName());
    }

    @Test
    void testGetterAndSetter_description() {
        LayerRequest request = new LayerRequest();
        request.setDescription("This is a test layer.");
        assertEquals("This is a test layer.", request.getDescription());
    }

    @Test
    void testGetterAndSetter_query() {
        LayerRequest request = new LayerRequest();
        request.setQuery("SELECT * WHERE {?s ?p ?o}");
        assertEquals("SELECT * WHERE {?s ?p ?o}", request.getQuery());
    }

    @Test
    void testNameNotBlank() {
        LayerRequest request = new LayerRequest();
        request.setQuery("Test");
        Set<ConstraintViolation<LayerRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        ConstraintViolation<LayerRequest> violation = violations.iterator().next();
        assertEquals("Name cannot be blank", violation.getMessage());
    }

    @Test
    void testQueryNotBlank() {
        LayerRequest request = new LayerRequest();
        request.setName("Test Layer");
        Set<ConstraintViolation<LayerRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        ConstraintViolation<LayerRequest> violation = violations.iterator().next();
        assertEquals("Query cannot be blank", violation.getMessage());
    }

    @Test
    void testBlankDescription() {
        LayerRequest request = new LayerRequest();
        request.setName("Test Layer");
        request.setDescription("");
        request.setQuery("Test");
        Set<ConstraintViolation<LayerRequest>> violations = validator.validate(request);
        assertEquals(0, violations.size());  // Empty strings are allowed
    }

    @Test
    void testNullDescription() {
        LayerRequest request = new LayerRequest();
        request.setName("Test Layer");
        request.setDescription(null);
        request.setQuery("Test");
        Set<ConstraintViolation<LayerRequest>> violations = validator.validate(request);
        assertEquals(0, violations.size());  // Null values are allowed
    }

}
