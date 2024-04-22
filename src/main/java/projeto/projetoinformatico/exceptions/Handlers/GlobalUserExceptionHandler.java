package projeto.projetoinformatico.exceptions.Handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import projeto.projetoinformatico.exceptions.Exception.InvalidParamsRequestException;
import projeto.projetoinformatico.exceptions.Exception.NotFoundException;
import projeto.projetoinformatico.responses.ErrorResponse;

@ControllerAdvice
public class GlobalUserExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse errorResponse = new ErrorResponse(status.value(), status, ex.getMessage());
        return new ResponseEntity<>(errorResponse, status);
    }
    @ExceptionHandler(InvalidParamsRequestException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleInvalidUserParams(NotFoundException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
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
