package projeto.projetoinformatico.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import projeto.projetoinformatico.exceptions.Exception.JwtAuthenticationException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {
        //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Authentication failed");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Unauthorized: " + authException.getMessage());
        response.getWriter().write("Unauthorized: " + authException.getMessage());
    }

/*
    private ObjectMapper objectMapper;

    public JWTAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JWTAuthenticationEntryPoint() {
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        // Set response status
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Create a map to hold status and message
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("status", String.valueOf(HttpServletResponse.SC_UNAUTHORIZED));
        responseBody.put("message", "Unauthorized: " + authException.getMessage());

        // Set content type to application/json
        response.setContentType("application/json");

        // Write response as JSON
        objectMapper.writeValue(response.getWriter(), responseBody);
    }

 */
}
