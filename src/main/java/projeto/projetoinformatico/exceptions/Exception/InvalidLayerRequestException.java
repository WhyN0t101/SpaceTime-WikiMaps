package projeto.projetoinformatico.exceptions.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidLayerRequestException extends RuntimeException {
    public InvalidLayerRequestException(String message) {
        super(message);
    }
}
