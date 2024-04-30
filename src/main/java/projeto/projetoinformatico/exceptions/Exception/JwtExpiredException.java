package projeto.projetoinformatico.exceptions.Exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class JwtExpiredException extends RuntimeException{//alterar
    public JwtExpiredException(String message) {
        super(message);
    }
}
