package projeto.projetoinformatico.responses;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import projeto.projetoinformatico.responses.ErrorResponse;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void testConstructorAndAccessors() {
        LocalDateTime timestamp = LocalDateTime.now();
        int errorCode = 404;
        HttpStatus status = HttpStatus.NOT_FOUND;
        String message = "Resource not found";

        ErrorResponse errorResponse = new ErrorResponse(errorCode, status, message);

        assertEquals(errorCode, errorResponse.getErrorCode());
        assertEquals(status, errorResponse.getStatus());
        assertEquals(message, errorResponse.getMessage());

        // Since timestamp is generated in the constructor, we can't directly compare it.
        // But we can check if it's not null.
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    void testSetters() {
        ErrorResponse errorResponse = new ErrorResponse();

        LocalDateTime timestamp = LocalDateTime.now();
        errorResponse.setTimestamp(timestamp);
        assertEquals(timestamp, errorResponse.getTimestamp());

        int errorCode = 500;
        errorResponse.setErrorCode(errorCode);
        assertEquals(errorCode, errorResponse.getErrorCode());

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        errorResponse.setStatus(status);
        assertEquals(status, errorResponse.getStatus());

        String message = "Internal Server Error";
        errorResponse.setMessage(message);
        assertEquals(message, errorResponse.getMessage());
    }
}
