package projeto.projetoinformatico.Exceptions.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class SparqlQueryExecutionException extends RuntimeException {
    public SparqlQueryExecutionException(String message) {
        super(message);
    }
}
