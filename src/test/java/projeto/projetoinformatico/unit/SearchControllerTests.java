package projeto.projetoinformatico.unit;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import projeto.projetoinformatico.controller.SearchController;
import projeto.projetoinformatico.model.SearchResult;
import projeto.projetoinformatico.service.SearchService;

import java.util.Collections;

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
        when(searchService.performSearch(10.0, 20.0, 100L, 200L))
                .thenReturn(new SearchResult(Collections.singletonList("Result")));

        // Call the controller method
        SearchResult result = searchController.performSearch(10.0, 20.0, 100L, 200L).getBody();

        // Verify that the result is as expected
        assertEquals(Collections.singletonList("Result"), result.getItemLabels());
    }
}
