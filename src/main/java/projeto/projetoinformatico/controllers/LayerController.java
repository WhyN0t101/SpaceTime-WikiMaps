package projeto.projetoinformatico.controllers;

import com.google.common.util.concurrent.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

/**
 * Controller class for handling layer operations.
 */
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

    /**
     * Retrieves all layers with pagination and optional search query.
     *
     * @param page  Page number (default is 0).
     * @param size  Number of items per page (default is 10).
     * @param query Optional query string for filtering.
     * @return ResponseEntity with a list of LayerDTOs and pagination information.
     */
    @Operation(summary = "Get all layers", description = "Retrieves all layers with pagination and optional search query.")
    @GetMapping("/layers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved layers"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
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

    /**
     * Retrieves a layer by its ID.
     *
     * @param id The ID of the layer to retrieve.
     * @return ResponseEntity with the retrieved LayerDTO.
     */
    @Operation(summary = "Get layer by ID", description = "Retrieves a layer by its ID.")
    @GetMapping("/layers/id/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the layer"),
            @ApiResponse(responseCode = "404", description = "Layer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<LayerDTO> getLayerById(
            @Parameter(description = "ID of the layer to retrieve", required = true)
            @PathVariable Long id) {
        LayerDTO layer = layerService.getLayerById(id);
        if (layer == null) {
            throw new NotFoundException("Layer not found with id: " + id);
        }
        return ResponseEntity.ok(layer);
    }

    /**
     * Retrieves search results for a layer by ID with specified parameters.
     *
     * @param id    The ID of the layer to search.
     * @param lat1  Latitude coordinate 1.
     * @param lon2  Longitude coordinate 2.
     * @param lat2  Latitude coordinate 2.
     * @param lon1  Longitude coordinate 1.
     * @param start Start timestamp for the query.
     * @param end   End timestamp for the query.
     * @return ResponseEntity with the search results.
     */
    @Operation(summary = "Get layer results with parameters", description = "Retrieves search results for a layer by ID with specified parameters.")
    @GetMapping("/layers/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved layer results"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getLayerResultsByIdWithParams(
            @Parameter(description = "ID of the layer to search", required = true)
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

    /**
     * Creates a new layer.
     *
     * @param layerRequest The LayerRequest object containing layer details.
     * @return ResponseEntity with the created LayerDTO.
     */
    @Operation(summary = "Create a layer", description = "Creates a new layer.")
    @PostMapping("/layers")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Layer successfully created"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<LayerDTO> createLayer(
            @Parameter(description = "Layer details", required = true)
            @Valid @RequestBody LayerRequest layerRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        LayerDTO newLayer = layerService.createLayer(username, layerRequest);
        return ResponseEntity.ok(newLayer);
    }

    /**
     * Updates an existing layer.
     *
     * @param id           The ID of the layer to update.
     * @param layerRequest The LayerRequest object containing updated layer details.
     * @return ResponseEntity with the updated LayerDTO.
     */
    @Operation(summary = "Update layer by ID", description = "Updates an existing layer by ID.")
    @PutMapping("/layers/{id}")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Layer successfully updated"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<LayerDTO> updateLayer(
            @Parameter(description = "ID of the layer to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated layer details", required = true)
            @Valid @RequestBody LayerRequest layerRequest) {
        LayerDTO updatedLayer = layerService.updateLayer(id, layerRequest);
        return ResponseEntity.ok(updatedLayer);
    }

    /**
     * Deletes a layer by ID.
     *
     * @param id The ID of the layer to delete.
     * @return ResponseEntity with no content upon successful deletion.
     */
    @Operation(summary = "Delete layer by ID", description = "Deletes a layer by its ID.")
    @DeleteMapping("/layers/{id}")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Layer successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteLayer(
            @Parameter(description = "ID of the layer to delete", required = true)
            @PathVariable Long id) {
        layerService.deleteLayer(id);
        return ResponseEntity.noContent().build();
    }
}
