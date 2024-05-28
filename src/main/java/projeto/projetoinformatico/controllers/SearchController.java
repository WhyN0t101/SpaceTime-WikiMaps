package projeto.projetoinformatico.controllers;

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

import java.util.Collections;

@RestController
@RequestMapping("/api")
@Validated
public class SearchController {

    private final SearchService searchService;
    private static final double REQUESTS_PER_SECOND = 20.0;
    private static final RateLimiter rateLimiter = RateLimiter.create(REQUESTS_PER_SECOND);

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping("/sparql")
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
