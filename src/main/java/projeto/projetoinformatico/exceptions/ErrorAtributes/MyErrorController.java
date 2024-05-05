package projeto.projetoinformatico.exceptions.ErrorAtributes;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import projeto.projetoinformatico.responses.ErrorResponse;

import java.util.HashMap;
import java.util.Map;

@Component
public class MyErrorController extends BasicErrorController {
    public MyErrorController(
            ErrorAttributes errorAttributes, ServerProperties serverProperties) {
        super(errorAttributes, serverProperties.getError());
    }

    /*
    @RequestMapping(produces = "application/json")
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        Throwable error = (Throwable) request.getAttribute("javax.servlet.error.exception");
        if (error instanceof JwtException) {
            // JWT token is expired, customize the error response
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("error", "Your session has expired");
            responseBody.put("details", "Please log in again to continue.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseBody);
        } else {
            // Handle other errors using the default behavior
            return super.error(request);
        }
    }
     */
}
