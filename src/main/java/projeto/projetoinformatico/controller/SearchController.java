package projeto.projetoinformatico.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import projeto.projetoinformatico.model.SearchResult;
import projeto.projetoinformatico.service.SearchService;

@RestController
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public ResponseEntity<SearchResult> performSearch(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam Long startTime,
            @RequestParam Long endTime
    ) {
        // Call the search service to perform the search
        SearchResult searchResult = searchService.performSearch(latitude, longitude, startTime, endTime);

        // Check if search result is not null
        if (searchResult != null) {
            return ResponseEntity.ok(searchResult);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
