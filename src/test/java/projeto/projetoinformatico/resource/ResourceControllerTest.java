package projeto.projetoinformatico.resource;

import com.github.jsonldjava.shaded.com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import projeto.projetoinformatico.controllers.ResourceController;
import projeto.projetoinformatico.controllers.UserController;
import projeto.projetoinformatico.model.SearchResult;
import projeto.projetoinformatico.model.users.UserRepository;
import projeto.projetoinformatico.service.ResourceService;
import projeto.projetoinformatico.service.SearchService;
import projeto.projetoinformatico.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ResourceControllerTest {

    private ResourceService resourceService;
    private ResourceController resourceController;
    private RateLimiter rateLimiter;
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        resourceService = mock(ResourceService.class);
        userRepository = mock(UserRepository.class);
        rateLimiter = mock(RateLimiter.class);
        resourceController = new ResourceController(resourceService);
    }

    @Test
    public void testGetWikidataItem_Success() {
        // Mock rateLimiter.tryAcquire() to return true
        when(rateLimiter.tryAcquire()).thenReturn(true);

        String itemId = "yourItemId";
        List<Map<String, String>> mockResults = Collections.emptyList(); // Mock search results
        SearchResult mockSearchResult = new SearchResult(mockResults);
        when(resourceService.getItem(itemId)).thenReturn(mockSearchResult);

        ResponseEntity<SearchResult> responseEntity = resourceController.getWikidataItem(itemId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockResults, responseEntity.getBody().results());
    }

    /*
    @Test//Fails
    public void testGetWikidataItem_TooManyRequests() {
        // Mock rateLimiter.tryAcquire() to return false
        when(rateLimiter.tryAcquire()).thenReturn(false);

        String itemId = "yourItemId";

        ResponseEntity<SearchResult> responseEntity = resourceController.getWikidataItem(itemId);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, responseEntity.getStatusCode()); // Corrected assertion
        verify(resourceService, never()).getItem(itemId); // Verify that getItem() is never called
    }
    */

    @Test
    public void testGetWikidataProperty_Success() {
        // Mock rateLimiter.tryAcquire() to return true
        when(rateLimiter.tryAcquire()).thenReturn(true);

        String propertyId = "yourPropertyId";
        List<Map<String, String>> mockResults = Collections.emptyList(); // Mock search results
        SearchResult mockSearchResult = new SearchResult(mockResults);
        when(resourceService.getProperty(propertyId)).thenReturn(mockSearchResult);

        ResponseEntity<SearchResult> responseEntity = resourceController.getWikidataProperty(propertyId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockResults, responseEntity.getBody().results());
    }

    /*
    @Test
    public void testGetWikidataProperty_TooManyRequests() {
        // Mock rateLimiter.tryAcquire() to return false
        when(rateLimiter.tryAcquire()).thenReturn(false);

        String propertyId = "yourPropertyId";

        ResponseEntity<SearchResult> responseEntity = resourceController.getWikidataProperty(propertyId);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, responseEntity.getStatusCode()); // Corrected assertion
        verify(resourceService, never()).getProperty(propertyId); // Verify that getProperty() is never called
    }
*/

    @Test
    public void testGetGeolocationData_Success() {
        // Mock rateLimiter.tryAcquire() to return true
        when(rateLimiter.tryAcquire()).thenReturn(true);

        String itemId = "yourItemId";
        List<Map<String, String>> mockResults = Collections.emptyList(); // Mock search results
        SearchResult mockSearchResult = new SearchResult(mockResults); // Mock search results
        when(resourceService.getGeolocationData(itemId)).thenReturn(mockSearchResult);

        ResponseEntity<?> responseEntity = resourceController.getGeolocationData(itemId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockSearchResult, responseEntity.getBody());
    }

    /*
    @Test
    public void testGetGeolocationData_TooManyRequests() {
        // Mock rateLimiter.tryAcquire() to return false
        when(rateLimiter.tryAcquire()).thenReturn(false);

        String itemId = "yourItemId";

        ResponseEntity<?> responseEntity = resourceController.getGeolocationData(itemId);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, responseEntity.getStatusCode());
        verify(resourceService, never()).getGeolocationData(itemId); // Verify that getGeolocationData() is never called
    }*/
    @Test
    public void testGetPropertyValues_Success() {
        // Mock rateLimiter.tryAcquire() to return true
        when(rateLimiter.tryAcquire()).thenReturn(true);

        String itemId = "yourItemId";
        String propertyId = "yourPropertyId";
        List<Map<String, String>> mockResults = Collections.emptyList(); // Mock search results
        SearchResult mockSearchResult = new SearchResult(mockResults); // Mock search results
        when(resourceService.getPropertyValues(itemId, propertyId)).thenReturn(mockSearchResult);

        ResponseEntity<?> responseEntity = resourceController.getPropertyValues(itemId, propertyId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockSearchResult, responseEntity.getBody());
    }
    /*
    @Test
    public void testGetPropertyValues_TooManyRequests() {
        // Mock rateLimiter.tryAcquire() to return false
        when(rateLimiter.tryAcquire()).thenReturn(false);

        String itemId = "yourItemId";
        String propertyId = "yourPropertyId";

        ResponseEntity<?> responseEntity = resourceController.getPropertyValues(itemId, propertyId);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, responseEntity.getStatusCode());
        verify(resourceService, never()).getPropertyValues(itemId, propertyId); // Verify that getPropertyValues() is never called
    }
*/
}
