package projeto.projetoinformatico.resource;

import com.github.jsonldjava.shaded.com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import projeto.projetoinformatico.controllers.ResourceController;
import projeto.projetoinformatico.exceptions.Exception.SparqlQueryNotFoundException;
import projeto.projetoinformatico.model.SearchResult;
import projeto.projetoinformatico.model.users.UserRepository;
import projeto.projetoinformatico.service.ResourceService;
import projeto.projetoinformatico.service.SearchService;
import projeto.projetoinformatico.utils.SparqlQueryProvider;

import java.util.*;

import static org.eclipse.rdf4j.model.util.Configurations.getPropertyValues;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ResourceServiceTest {
    private ResourceService resourceService;
    private SearchService searchService;
    private SparqlQueryProvider sparqlQueryProvider;

    @BeforeEach
    public void setUp() {
        searchService = mock(SearchService.class);
        sparqlQueryProvider = mock(SparqlQueryProvider.class);
        resourceService = new ResourceService(searchService, sparqlQueryProvider);
    }

    @Test
    public void testGetPropertyValues_Success() {
        // Given
        String itemId = "exampleItemId";
        String propertyId = "examplePropertyId";
        when(sparqlQueryProvider.buildPropertyItemQuery(anyString(), anyString())).thenReturn("exampleSparqlQuery");

        SearchResult emptySearchResult = new SearchResult(Collections.emptyList());

        when(searchService.executeSparqlQuery(anyString())).thenReturn(emptySearchResult);

        // When
        SearchResult result = resourceService.getPropertyValues(itemId, propertyId);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.results().isEmpty());
    }

    @Test
    public void testGetGeolocationData_Success() {
        // Given
        String itemId = "exampleItemId";
        when(sparqlQueryProvider.buildGeoQuery(anyString())).thenReturn("exampleGeoSparqlQuery");

        SearchResult emptySearchResult = new SearchResult(Collections.emptyList());

        when(searchService.executeSparqlQuery(anyString())).thenReturn(emptySearchResult);

        // When
        SearchResult result = resourceService.getGeolocationData(itemId);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.results().isEmpty());
    }
    @Test
    public void testGetItem_Success() {
        // Given
        String itemId = "exampleItemId";
        when(sparqlQueryProvider.buildItemQuery(anyString())).thenReturn("exampleItemSparqlQuery");

        SearchResult searchResult = new SearchResult(Collections.singletonList(
                new HashMap<>() {{
                    put("exampleKey1", "exampleValue1");
                }}
        ));

        when(searchService.executeSparqlQuery(anyString())).thenReturn(searchResult);

        // When
        SearchResult result = resourceService.getItem(itemId);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.results().isEmpty());
        Assertions.assertEquals("exampleValue1", result.results().get(0).get("exampleKey1"));
    }

    @Test
    public void testGetProperty_Success() {
        // Given
        String propertyId = "examplePropertyId";
        when(sparqlQueryProvider.buildPropertyQuery(anyString())).thenReturn("examplePropertySparqlQuery");

        SearchResult searchResult = new SearchResult(Collections.singletonList(
                new HashMap<>() {{
                    put("exampleKey2", "exampleValue2");
                }}
        ));

        when(searchService.executeSparqlQuery(anyString())).thenReturn(searchResult);

        // When
        SearchResult result = resourceService.getProperty(propertyId);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.results().isEmpty());
        Assertions.assertEquals("exampleValue2", result.results().get(0).get("exampleKey2"));
    }

    @Test
    public void testExecuteSparqlQuery_Error() {
        // Given
        when(searchService.executeSparqlQuery(anyString())).thenReturn(null);

        // When and Then
        Assertions.assertThrows(SparqlQueryNotFoundException.class, () -> {
            resourceService.getPropertyValues("exampleItemId", "examplePropertyId");
        });
    }


}
