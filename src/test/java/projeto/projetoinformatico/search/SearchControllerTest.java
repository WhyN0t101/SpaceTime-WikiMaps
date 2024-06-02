package projeto.projetoinformatico.search;

import com.github.jsonldjava.shaded.com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import projeto.projetoinformatico.controllers.SearchController;
import projeto.projetoinformatico.service.SearchService;
import projeto.projetoinformatico.model.SearchResult;
import projeto.projetoinformatico.utils.Validation;


import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class SearchControllerTest {

    private SearchController searchController;
    private SearchService searchService;
    private Validation validation;
    private RateLimiter rateLimiter;


    @BeforeEach
    public void setUp() {
        searchService = mock(SearchService.class);
        validation = mock(Validation.class);
        rateLimiter = mock(RateLimiter.class);
        searchController = new SearchController(searchService);
    }

    @Test
    public void testExecuteSparqlQuery_Success() {
        // Mock rateLimiter.tryAcquire() to return true
        when(rateLimiter.tryAcquire()).thenReturn(true);

        String sparqlQuery = "{\"query\": \"Your SPARQL query here\"}"; // Example SPARQL query
        List<Map<String, String>> mockResults = Collections.emptyList(); // Mock search results
        SearchResult mockSearchResult = new SearchResult(mockResults); // Mock search results
        when(searchService.executeSparqlQueryFromJsonString(sparqlQuery)).thenReturn(mockSearchResult);

        ResponseEntity<SearchResult> responseEntity = searchController.executeSparqlQuery(sparqlQuery);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockSearchResult, responseEntity.getBody());
    }

    /*
    @Test
    public void testExecuteSparqlQuery_TooManyRequests() {
        // Mock rateLimiter.tryAcquire() to return false
        when(rateLimiter.tryAcquire()).thenReturn(false);

        String sparqlQuery = "{\"query\": \"Your SPARQL query here\"}"; // Example SPARQL query

        ResponseEntity<SearchResult> responseEntity = searchController.executeSparqlQuery(sparqlQuery);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, responseEntity.getStatusCode());
        verify(searchService, never()).executeSparqlQueryFromJsonString(sparqlQuery); // Verify that executeSparqlQueryFromJsonString() is never called
    }
     */
}
