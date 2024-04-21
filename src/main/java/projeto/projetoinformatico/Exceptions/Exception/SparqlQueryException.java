package projeto.projetoinformatico.Exceptions.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class SparqlQueryException extends RuntimeException {
    public SparqlQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
