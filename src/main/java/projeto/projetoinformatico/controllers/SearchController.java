package projeto.projetoinformatico.controllers;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import projeto.projetoinformatico.model.SearchResult;
import projeto.projetoinformatico.service.SearchService;
import projeto.projetoinformatico.exceptions.Exception.SparqlQueryException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.Collections;

@RestController
@RequestMapping("/api")
@Validated
public class SearchController {

    private final SearchService searchService;
    @Value("${max.requests}")
    private static int REQUESTS_PER_SECOND;

    private static final RateLimiter rateLimiter = RateLimiter.create(REQUESTS_PER_SECOND);

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Endpoint to execute a SPARQL query.
     *
     * @param sparqlQuery The SPARQL query string.
     * @return ResponseEntity with the search result.
     */
    @Operation(summary = "Execute SPARQL query", description = "Endpoint to execute a SPARQL query.")
    @PostMapping("/sparql")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful execution of SPARQL query"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "429", description = "Too Many Requests"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SearchResult> executeSparqlQuery(
            @Parameter(description = "SPARQL query string", required = true)
            @RequestBody String sparqlQuery) {
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
