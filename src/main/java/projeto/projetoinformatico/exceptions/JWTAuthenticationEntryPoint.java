package projeto.projetoinformatico.exceptions;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import projeto.projetoinformatico.exceptions.Exception.JwtExpiredException;
import java.io.IOException;


public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Exception exception = (Exception) request.getAttribute("exception");

        if (exception instanceof JwtExpiredException) {
            response.getWriter().write("JWT expired");
        } else {
            response.getWriter().write("Unauthorized: " + authException.getMessage());
        }
    }


}
