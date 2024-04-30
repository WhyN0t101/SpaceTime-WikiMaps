package projeto.projetoinformatico.exceptions.Exception;


import jakarta.servlet.ServletException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class JwtExpiredException extends ServletException {
    public JwtExpiredException(String message) {
        super(message);
    }
}
