package projeto.projetoinformatico.controller;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import projeto.projetoinformatico.model.SearchResult;
import projeto.projetoinformatico.service.SearchService;

import java.time.Year;

@RestController
@Validated
public class SearchController {

    private final SearchService searchService;
    private static final double REQUESTS_PER_SECOND = 20.0; // Set the desired rate here
    private static final RateLimiter rateLimiter = RateLimiter.create(REQUESTS_PER_SECOND);

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/api/search")
    public ResponseEntity<SearchResult> performSearch(
            @RequestParam Double lat1,
            @RequestParam Double lon2,
            @RequestParam Double lat2,
            @RequestParam Double lon1
    ) {
        if (!rateLimiter.tryAcquire()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        if (!isValidCoordinate(lat1, lon2, lat2, lon1)) {
            return ResponseEntity.badRequest().build();
        }
        SearchResult searchResult = searchService.performSearch(lat1, lon1, lat2, lon2);
        return (searchResult != null) ? ResponseEntity.ok(searchResult) : ResponseEntity.notFound().build();
    }

    @GetMapping("/api/search/time")
    public ResponseEntity<SearchResult> performSearchTime(
            @RequestParam Double lat1,
            @RequestParam Double lon2,
            @RequestParam Double lat2,
            @RequestParam Double lon1,
            @RequestParam Long startTime,
            @RequestParam Long endTime
    ) {
        if (!rateLimiter.tryAcquire()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        if (!isValidCoordinate(lat1, lon2, lat2, lon1) || !isValidYearRange(startTime, endTime)) {
            return ResponseEntity.badRequest().build();
        }
        SearchResult searchResult = searchService.performSearchTime(lat1, lon1, lat2, lon2, startTime, endTime);
        return (searchResult != null) ? ResponseEntity.ok(searchResult) : ResponseEntity.notFound().build();
    }

    @GetMapping("/api/search/country")
    public ResponseEntity<SearchResult> performSearchCountry(
            @RequestParam String country,
            @RequestParam Long year
    ) {
        if (!rateLimiter.tryAcquire()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        if (!isValidYear(year)) {
            return ResponseEntity.badRequest().build();
        }
        SearchResult searchResult = searchService.performSearchYear(country, year);
        return (searchResult != null) ? ResponseEntity.ok(searchResult) : ResponseEntity.notFound().build();
    }

    @PostMapping("/api/sparql")
    public ResponseEntity<SearchResult> executeSparqlQuery(@RequestBody String sparqlQuery) {
        try {
            SearchResult searchResult = searchService.perfomSparqlQuery(sparqlQuery);
            return ResponseEntity.ok(searchResult);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private boolean isValidCoordinate(Double lat1, Double lon1, Double lat2, Double lon2) {
        return lat1 != null && lon1 != null && lat2 != null && lon2 != null &&
                lat1 >= -90 && lat1 <= 90 && lon1 >= -180 && lon1 <= 180 &&
                lat2 >= -90 && lat2 <= 90 && lon2 >= -180 && lon2 <= 180;
    }

    private boolean isValidYear(Long year) {
        final int MIN_YEAR = 0;  // Assuming year 0 or later is valid
        final int MAX_YEAR = Year.now().getValue();  // Get the current year
        return year != null && year >= MIN_YEAR && year <= MAX_YEAR;
    }

    private boolean isValidYearRange(Long startYear, Long endYear) {
        return isValidYear(startYear) && isValidYear(endYear) && startYear <= endYear;
    }
}
