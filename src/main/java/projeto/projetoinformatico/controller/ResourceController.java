package projeto.projetoinformatico.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import projeto.projetoinformatico.model.SearchResult;
import projeto.projetoinformatico.service.ResourceService;
@Validated
@RestController
@RequestMapping("/api")
public class ResourceController {

    private final ResourceService resourceService;

    @Autowired
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping("/items/{itemId}")
    public ResponseEntity<SearchResult> getWikidataItem(@PathVariable String itemId) {
        SearchResult item = resourceService.getItem(itemId);
        return ResponseEntity.ok(item);
    }

    @GetMapping("/properties/{propertyId}")
    public ResponseEntity<SearchResult> getWikidataProperty(@PathVariable String propertyId) {
       SearchResult property = resourceService.getProperty(propertyId);
        return ResponseEntity.ok(property);
    }
}
