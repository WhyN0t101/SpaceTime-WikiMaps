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

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class SearchControllerTests {

    @Mock
    private SearchService searchService; // Mock the SearchService dependency

    @InjectMocks
    private SearchController searchController; // Inject the mocked dependencies into the controller

    public SearchControllerTests() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    public void testPerformSearch() {
        // Mock the behavior of the searchService.performSearch method
        when(searchService.performSearch(49.0, (double) -124, 24.0, -81.0))
                .thenReturn(new SearchResult(Collections.singletonList(Map.of("key", "value"))));

        // Call the controller method with the required parameters
        ResponseEntity<SearchResult> responseEntity = searchController.performSearch(49.0, -81.0, 24.0, -124.0);

        // Verify that the result is as expected
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        SearchResult result = responseEntity.getBody();
        assertEquals(Collections.singletonList(Map.of("key", "value")), result.getItemLabels());
    }
}
