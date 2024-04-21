package projeto.projetoinformatico.Exceptions.Handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import projeto.projetoinformatico.Exceptions.Exception.SparqlQueryExecutionException;
import projeto.projetoinformatico.utils.ErrorResponse;
import projeto.projetoinformatico.utils.SparqlQueryException; // Import the SPARQL exception

@ControllerAdvice
public class GlobalSparqlExceptionHandler {

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
