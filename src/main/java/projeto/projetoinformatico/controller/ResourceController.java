package projeto.projetoinformatico.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import projeto.projetoinformatico.model.SearchResult;
import projeto.projetoinformatico.service.ResourceService;
@Validated
@RestController
@RequestMapping("/api")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping("/items/{itemId}")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<SearchResult> getWikidataItem(@PathVariable String itemId) {
        SearchResult item = resourceService.getItem(itemId);
        return ResponseEntity.ok(item);
    }

    @GetMapping("/properties/{propertyId}")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<SearchResult> getWikidataProperty(@PathVariable String propertyId) {
       SearchResult property = resourceService.getProperty(propertyId);
        return ResponseEntity.ok(property);
    }
    @GetMapping("/data/geolocation/{item_id}")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<?> getGeolocationData(@PathVariable("item_id") String itemId) {
        try {
            SearchResult geolocationData = resourceService.getGeolocationData(itemId);
            return ResponseEntity.ok(geolocationData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/data/property-values/{item_id}/{property_id}")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<?> getPropertyValues(@PathVariable("item_id") String itemId,
                                               @PathVariable("property_id") String propertyId) {
        try {
            SearchResult propertyValue = resourceService.getPropertyValues(itemId, propertyId);
            return ResponseEntity.ok(propertyValue);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
