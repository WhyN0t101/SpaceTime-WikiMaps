package projeto.projetoinformatico.exceptions.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidParamsRequestException extends RuntimeException {
    public InvalidParamsRequestException(String message) {
        super(message);
    }
}
