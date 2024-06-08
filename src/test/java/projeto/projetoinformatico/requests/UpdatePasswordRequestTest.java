package projeto.projetoinformatico.requests;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdatePasswordRequestTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void testGetterAndSetter_newPassword() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setNewPassword("newPassword123");
        assertEquals("newPassword123", request.getNewPassword());
    }

    @Test
    void testGetterAndSetter_oldPassword() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setOldPassword("oldPassword456");
        assertEquals("oldPassword456", request.getOldPassword());
    }

    @Test
    void testEqualsAndHashCode() {
        UpdatePasswordRequest request1 = new UpdatePasswordRequest();
        request1.setNewPassword("password123");
        request1.setOldPassword("oldPassword");

        UpdatePasswordRequest request2 = new UpdatePasswordRequest();
        request2.setNewPassword("password123");
        request2.setOldPassword("oldPassword");

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }
    @Test
    void testToString() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setNewPassword("newPassword123");
        request.setOldPassword("oldPassword456");

        String expected = "UpdatePasswordRequest(newPassword=newPassword123, oldPassword=oldPassword456)";
        assertEquals(expected, request.toString());
    }

    @Test
    void testNotBlankValidation() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        Set<ConstraintViolation<UpdatePasswordRequest>> violations = validator.validate(request);
        assertEquals(2, violations.size()); // Both newPassword and oldPassword should not be blank
    }

    @Test
    void testNewPasswordMinSizeValidation() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setOldPassword("1234567");
        request.setNewPassword("12345"); // Less than minimum size (6)
        Set<ConstraintViolation<UpdatePasswordRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

}
