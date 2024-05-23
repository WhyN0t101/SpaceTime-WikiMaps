package projeto.projetoinformatico.controllers;

import com.google.common.util.concurrent.RateLimiter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import projeto.projetoinformatico.dtos.LayerDTO;
import projeto.projetoinformatico.dtos.Paged.LayerPageDTO;
import projeto.projetoinformatico.exceptions.Exception.InvalidParamsRequestException;
import projeto.projetoinformatico.exceptions.Exception.NotFoundException;
import projeto.projetoinformatico.exceptions.Exception.SparqlQueryException;
import projeto.projetoinformatico.model.SearchResult;
import projeto.projetoinformatico.requests.LayerRequest;
import projeto.projetoinformatico.service.LayerService;
import projeto.projetoinformatico.utils.Validation;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
public class LayerController {

    private final LayerService layerService;
    private static final double REQUESTS_PER_SECOND = 20.0;

    private static final RateLimiter rateLimiter = RateLimiter.create(REQUESTS_PER_SECOND);
    private final Validation validation;

    public LayerController(LayerService layerService, Validation validation) {
        this.layerService = layerService;
        this.validation = validation;
    }

    @GetMapping("/layers")
    public ResponseEntity<LayerPageDTO> getAllLayers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String query) {
        if(size < 1){
            throw new InvalidParamsRequestException("Invalid size of pagination");
        }
        if(page < 0){
            throw new InvalidParamsRequestException("Invalid page of pagination");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<LayerDTO> layers;
        if(query != null){
            layers =  layerService.findByKeywordsPaged(query,pageable);
        }else{
            layers = layerService.getAllLayersPaged(pageable);
        }
        LayerPageDTO response = new LayerPageDTO(
                layers.getContent(),
                layers.getNumber(),
                (int) layers.getTotalElements(),
                layers.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/layers/id/{id}")
    public ResponseEntity<LayerDTO> getLayerById(@PathVariable Long id) {
        LayerDTO layer = layerService.getLayerById(id);
        if (layer == null) {
            throw new NotFoundException("Layer not found with id: " + id);
        }
        return ResponseEntity.ok(layer);
    }

    @GetMapping("/layers/{id}")
    public ResponseEntity<?> getLayerResultsByIdWithParams(
            @PathVariable Long id,
            @RequestParam Double lat1,
            @RequestParam Double lon2,
            @RequestParam Double lat2,
            @RequestParam Double lon1,
            @RequestParam Long start,
            @RequestParam Long end
    ) {
        if (!rateLimiter.tryAcquire()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        if (!validation.isValidCoordinate(lat1, lon2, lat2, lon1)) {
            throw new InvalidParamsRequestException("Invalid params");
        }
        try {
            SearchResult searchResult = layerService.getLayerByIdWithParams(id, lat1, lon1, lat2, lon2, start, end); // Call the service method
            return ResponseEntity.ok(searchResult);
        } catch (SparqlQueryException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SearchResult(Collections.emptyList()));
        } catch (Exception e) {
            throw new SparqlQueryException("Invalid Sparql Query");
        }
    }

    @PostMapping("/layers")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    public ResponseEntity<LayerDTO> createLayer(@Valid @RequestBody LayerRequest layerRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        LayerDTO newLayer = layerService.createLayer(username, layerRequest);
        return ResponseEntity.ok(newLayer);
    }

    @PutMapping("/layers/{id}")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<LayerDTO> updateLayer(@PathVariable Long id, @Valid @RequestBody LayerRequest layerRequest) {
        LayerDTO updatedLayer = layerService.updateLayer(id, layerRequest);
        return ResponseEntity.ok(updatedLayer);
    }

    @DeleteMapping("/layers/{id}")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<Void> deleteLayer(@PathVariable Long id) {
        layerService.deleteLayer(id);
        return ResponseEntity.noContent().build();
    }
}
