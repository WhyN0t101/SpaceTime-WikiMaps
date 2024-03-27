package projeto.projetoinformatico.controller;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import projeto.projetoinformatico.model.SearchResult;
import projeto.projetoinformatico.service.SearchService;

@RestController
@Validated
public class SearchController {

    private final SearchService searchService;
    private static final double REQUESTS_PER_SECOND = 20.0; // Set the desired rate here

    private static RateLimiter rateLimiter = RateLimiter.create(REQUESTS_PER_SECOND);

    // Inject SearchService
    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public ResponseEntity<SearchResult> performSearch(
            @RequestParam Double lat1,
            @RequestParam Double lon2,
            @RequestParam Double lat2,
            @RequestParam Double lon1
    ) {
        // Acquire a permit from RateLimiter
        if (!rateLimiter.tryAcquire()) {
            // If rate limit exceeded, return 429 Too Many Requests
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null);
        }

        // Call the search service to perform the search
        SearchResult searchResult = searchService.performSearch(lat1, lon1, lat2, lon2);

        // Check if search result is not null
        if (searchResult != null) {
            return ResponseEntity.ok(searchResult);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/search/time")
    public ResponseEntity<SearchResult> performSearchTime(
            @RequestParam Double lat1,
            @RequestParam Double lon2,
            @RequestParam Double lat2,
            @RequestParam Double lon1,
            @RequestParam Long startTime,
            @RequestParam Long endTime
    ) {
        // Acquire a permit from RateLimiter
        if (!rateLimiter.tryAcquire()) {
            // If rate limit exceeded, return 429 Too Many Requests
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null);
        }

        // Call the search service to perform the search
        SearchResult searchResult = searchService.performSearchTime(lat1, lon1, lat2, lon2, startTime, endTime);

        // Check if search result is not null
        if (searchResult != null) {
            return ResponseEntity.ok(searchResult);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
