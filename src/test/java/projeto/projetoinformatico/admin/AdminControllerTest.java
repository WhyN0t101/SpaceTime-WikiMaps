package projeto.projetoinformatico.admin;

import com.github.jsonldjava.shaded.com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import projeto.projetoinformatico.controllers.AdminController;
import projeto.projetoinformatico.service.UserService;
import projeto.projetoinformatico.utils.Validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class AdminControllerTest {

    @Test
    public void testSayHello_Success() {
        // Mock dependencies
        AdminController adminController = new AdminController();

        // Call the endpoint
        ResponseEntity<String> response = adminController.sayHello();

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Welcome Admin", response.getBody());
    }
}
