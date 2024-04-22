package projeto.projetoinformatico.exceptions.Handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import projeto.projetoinformatico.exceptions.Exception.InvalidParamsRequestException;
import projeto.projetoinformatico.exceptions.Exception.SparqlQueryException;
import projeto.projetoinformatico.exceptions.Exception.SparqlQueryExecutionException;
import projeto.projetoinformatico.exceptions.Exception.UserNotFoundException;
import projeto.projetoinformatico.responses.ErrorResponse;

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
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = new ErrorResponse(status.value(), status, "An unexpected error occurred");
        return new ResponseEntity<>(errorResponse, status);
    }
}
