package projeto.projetoinformatico.exceptions.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SparqlQueryNotFoundException extends RuntimeException {
    public SparqlQueryNotFoundException(String message) {
        super(message);
    }
}

