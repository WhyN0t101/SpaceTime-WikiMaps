package projeto.projetoinformatico.exceptions.controller;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import projeto.projetoinformatico.model.SearchResult;
import projeto.projetoinformatico.service.SearchService;
import projeto.projetoinformatico.exceptions.Exception.SparqlQueryException;
import projeto.projetoinformatico.utils.Validation;

import java.util.Collections;

@RestController
@RequestMapping("/api")
@Validated
public class SearchController {

    private final SearchService searchService;
    private final Validation validation;
    private static final double REQUESTS_PER_SECOND = 20.0; // Set the desired rate here
    private static final RateLimiter rateLimiter = RateLimiter.create(REQUESTS_PER_SECOND);

    @Autowired
    public SearchController(SearchService searchService, Validation validation) {
        this.searchService = searchService;
        this.validation = validation;
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<?> performSearch(
            @RequestParam Double lat1,
            @RequestParam Double lon2,
            @RequestParam Double lat2,
            @RequestParam Double lon1
    ) {
        if (!rateLimiter.tryAcquire()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        if (!validation.isValidCoordinate(lat1, lon2, lat2, lon1)) {
            return ResponseEntity.badRequest().body("Invalid coordinates provided");
        }
        try {
            SearchResult searchResult = searchService.performSearch(lat1, lon1, lat2, lon2);
            return ResponseEntity.ok(searchResult);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search/time")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<?> performSearchTime(
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
        if (!validation.isValidCoordinate(lat1, lon2, lat2, lon1) || !validation.isValidYearRange(startTime, endTime)) {
            return ResponseEntity.badRequest().body("Invalid coordinates or year provided");
        }
        try {
            SearchResult searchResult = searchService.performSearchTime(lat1, lon1, lat2, lon2, startTime, endTime);
            return ResponseEntity.ok(searchResult);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GetMapping("/search/country")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<?> performSearchCountry(
            @RequestParam String country,
            @RequestParam Long year
    ) {
        if (!rateLimiter.tryAcquire()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        if (!validation.isValidYear(year)) {
            return ResponseEntity.badRequest().body("Invalid coordinates provided");
        }
        try {
            SearchResult searchResult = searchService.performSearchYear(country, year);
            return ResponseEntity.ok(searchResult);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/sparql")
    //@PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<SearchResult> executeSparqlQuery(@RequestBody String sparqlQuery) {
        if (!rateLimiter.tryAcquire()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        try {
            SearchResult searchResult = searchService.executeSparqlQueryFromJsonString(sparqlQuery);
            return ResponseEntity.ok(searchResult);
        } catch (SparqlQueryException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SearchResult(Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
