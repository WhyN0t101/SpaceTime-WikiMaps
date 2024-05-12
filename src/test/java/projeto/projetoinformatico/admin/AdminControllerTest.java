package projeto.projetoinformatico.admin;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import projeto.projetoinformatico.controllers.AdminController;
import projeto.projetoinformatico.controllers.UserController;
import projeto.projetoinformatico.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
