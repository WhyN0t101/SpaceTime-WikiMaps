package projeto.projetoinformatico.exceptions.Handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import projeto.projetoinformatico.exceptions.Exception.*;
import projeto.projetoinformatico.responses.ErrorResponse;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse errorResponse = new ErrorResponse(status.value(), status, ex.getMessage());
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(InvalidParamsRequestException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleInvalidUserParams(InvalidParamsRequestException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = new ErrorResponse(status.value(), status, ex.getMessage());
        return new ResponseEntity<>(errorResponse, status);
    }
    @ExceptionHandler(SparqlQueryException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleSparqlQueryException(SparqlQueryException ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // Assuming SPARQL exceptions indicate internal server errors
        ErrorResponse errorResponse = new ErrorResponse(status.value(), status, ex.getMessage());
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(SparqlQueryExecutionException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleSparqlQueryExecutionException(SparqlQueryException ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // Assuming SPARQL exceptions indicate internal server errors
        ErrorResponse errorResponse = new ErrorResponse(status.value(), status, ex.getMessage());
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST; // Assuming SignUPRequest exceptions indicate internal server errors

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        String errorMessage = "Validation failed:";
        for (Map.Entry<String, String> entry : errors.entrySet()) {
            errorMessage += " " + entry.getValue() + ";";
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", errorMessage);
        //return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        ErrorResponse errorResponse = new ErrorResponse(status.value(), status, response.put("message", errorMessage));
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = new ErrorResponse(status.value(), status, "An unexpected error occurred");
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(JwtExpiredException.class)
    public ResponseEntity<ErrorResponse> handleJwtExpiredException(JwtExpiredException ex) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        ErrorResponse errorResponse = new ErrorResponse(status.value(), status, "JWT Token is expired. Please log in again.");
        return new ResponseEntity<>(errorResponse, status);
    }


}
