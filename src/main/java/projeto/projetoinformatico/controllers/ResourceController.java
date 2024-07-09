package projeto.projetoinformatico.controllers;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import projeto.projetoinformatico.model.SearchResult;
import projeto.projetoinformatico.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Validated
@RestController
@RequestMapping("/api")
public class ResourceController {

    private  final ResourceService resourceService;

    private final RateLimiter rateLimiter;

    @Autowired
    public ResourceController(ResourceService resourceService, @Value("${max.requests}") double requestsPerSecond) {
        this.resourceService = resourceService;
        this.rateLimiter = RateLimiter.create(requestsPerSecond);
    }

    /**
     * Endpoint to retrieve Wikidata item by ID.
     *
     * @param itemId The ID of the Wikidata item to retrieve.
     * @return ResponseEntity with the retrieved Wikidata item.
     */
    @Operation(summary = "Get Wikidata item by ID", description = "Endpoint to retrieve Wikidata item by ID.")
    @GetMapping("/items/{itemId}")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of Wikidata item"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "429", description = "Too Many Requests"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SearchResult> getWikidataItem(
            @Parameter(description = "Wikidata item ID to retrieve", required = true)
            @PathVariable String itemId) {
        if (!rateLimiter.tryAcquire()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        SearchResult item = resourceService.getItem(itemId);
        return ResponseEntity.ok(item);
    }

    /**
     * Endpoint to retrieve Wikidata property by ID.
     *
     * @param propertyId The ID of the Wikidata property to retrieve.
     * @return ResponseEntity with the retrieved Wikidata property.
     */
    @Operation(summary = "Get Wikidata property by ID", description = "Endpoint to retrieve Wikidata property by ID.")
    @GetMapping("/properties/{propertyId}")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of Wikidata property"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "429", description = "Too Many Requests"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SearchResult> getWikidataProperty(
            @Parameter(description = "Wikidata property ID to retrieve", required = true)
            @PathVariable String propertyId) {
        if (!rateLimiter.tryAcquire()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        SearchResult property = resourceService.getProperty(propertyId);
        return ResponseEntity.ok(property);
    }

    /**
     * Endpoint to retrieve geolocation data by item ID.
     *
     * @param itemId The ID of the item for geolocation data retrieval.
     * @return ResponseEntity with the retrieved geolocation data.
     */
    @Operation(summary = "Get geolocation data by item ID", description = "Endpoint to retrieve geolocation data by item ID.")
    @GetMapping("/data/geolocation/{item_id}")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of geolocation data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "429", description = "Too Many Requests"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getGeolocationData(
            @Parameter(description = "Item ID for geolocation data retrieval", required = true)
            @PathVariable("item_id") String itemId) {
        if (!rateLimiter.tryAcquire()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        try {
            SearchResult geolocationData = resourceService.getGeolocationData(itemId);
            return ResponseEntity.ok(geolocationData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint to retrieve property values by item ID and property ID.
     *
     * @param itemId     The ID of the item for property values retrieval.
     * @param propertyId The ID of the property for property values retrieval.
     * @return ResponseEntity with the retrieved property values.
     */
    @Operation(summary = "Get property values by item ID and property ID", description = "Endpoint to retrieve property values by item ID and property ID.")
    @GetMapping("/data/property-values/{item_id}/{property_id}")
    public ResponseEntity<?> getPropertyValues(
            @Parameter(description = "Item ID for property values retrieval", required = true)
            @PathVariable("item_id") String itemId,
            @Parameter(description = "Property ID for property values retrieval", required = true)
            @PathVariable("property_id") String propertyId) {
        if (!rateLimiter.tryAcquire()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        try {
            SearchResult propertyValue = resourceService.getPropertyValues(itemId, propertyId);
            return ResponseEntity.ok(propertyValue);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
