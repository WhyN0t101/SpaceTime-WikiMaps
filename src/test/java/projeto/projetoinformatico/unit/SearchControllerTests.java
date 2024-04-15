package projeto.projetoinformatico.unit;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import projeto.projetoinformatico.model.SearchResult;
import projeto.projetoinformatico.service.SearchService;
import projeto.projetoinformatico.controller.SearchController;
import projeto.projetoinformatico.utils.Validation; // Import Validation class

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class SearchControllerTests {

    @Mock
    private SearchService searchService; // Mock the SearchService dependency

    @Mock
    private Validation validation; // Mock the Validation dependency

    @InjectMocks
    private SearchController searchController; // Inject the mocked dependencies into the controller

    public SearchControllerTests() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    public void testPerformSearch() {
        // Mock the behavior of the searchService.performSearch method
        when(searchService.performSearch(-36.0, -6.0, 42.0, -9.0))
                .thenReturn(new SearchResult(Collections.singletonList(Map.of("key", "value"))));

        // Mock the behavior of the validation.isValidCoordinate method
        when(validation.isValidCoordinate(-36.0, -9.0, 42.0, -6.0)).thenReturn(true);

        // Call the controller method with the required parameters
        ResponseEntity<SearchResult> responseEntity = (ResponseEntity<SearchResult>) searchController.performSearch(-36.0, -9.0, 42.0, -6.0);

        // Verify that the result is as expected
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        SearchResult result = responseEntity.getBody();
        assert result != null;
        assertEquals(Collections.singletonList(Map.of("key", "value")), result.results());
    }


}
