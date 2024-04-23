package projeto.projetoinformatico.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import projeto.projetoinformatico.exceptions.Exception.NotFoundException;
import projeto.projetoinformatico.model.layers.Layer;
import projeto.projetoinformatico.requests.LayerRequest;
import projeto.projetoinformatico.service.LayerService;
import java.util.List;

@RestController
@RequestMapping("/api")
public class LayerController {

    private final LayerService layerService;

    public LayerController(LayerService layerService) {
        this.layerService = layerService;
    }

    @GetMapping("/layers")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<List<Layer>> getAllLayers() {
        List<Layer> layers = layerService.getAllLayers();
        return ResponseEntity.ok(layers);
    }

    @GetMapping("/layers/{id}")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<Layer> getLayerById(@PathVariable Long id) {
        Layer layer = layerService.getLayerById(id);
        if (layer == null) {
            throw new NotFoundException("Layer not found with id: " + id);
        }
        return ResponseEntity.ok(layer);
    }

    @PostMapping("/layers/create")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<Layer> createLayer(@RequestBody LayerRequest layerRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Layer newLayer = layerService.createLayer(username, layerRequest);
        return ResponseEntity.ok(newLayer);
    }

    @PutMapping("/layers/{id}")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<Layer> updateLayer(@PathVariable Long id, @RequestBody LayerRequest layerRequest) {
        Layer updatedLayer = layerService.updateLayer(id, layerRequest);
        return ResponseEntity.ok(updatedLayer);
    }

    @DeleteMapping("/layers/{id}")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<Void> deleteLayer(@PathVariable Long id) {
        layerService.deleteLayer(id);
        return ResponseEntity.noContent().build();
    }


}
