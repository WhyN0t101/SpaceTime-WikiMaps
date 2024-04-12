package projeto.projetoinformatico.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projeto.projetoinformatico.model.Layer;
import projeto.projetoinformatico.model.LayerRequest;
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
    public ResponseEntity<List<Layer>> getAllLayers() {
        // Retrieve all layers from the service layer
        List<Layer> layers = layerService.getAllLayers();

        // Check if layers exist and return response accordingly
        if (layers.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(layers);
        }
    }

    @GetMapping("/layers/{id}")
    public ResponseEntity<List<Record>> getLayerRecords(
            @PathVariable Long id,
            @RequestParam String location,
            @RequestParam String date_start,
            @RequestParam String date_end
    ) {
        // Retrieve layer records based on id, location, date_start, and date_end
        List<Record> layerRecords = layerService.getLayerRecords(id, location, date_start, date_end);

        // Check if layer records exist and return response accordingly
        if (layerRecords.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(layerRecords);
        }
    }
    @PostMapping("/layers/create")
    public ResponseEntity<Layer> createLayer(@RequestBody LayerRequest layerRequest) {
        Layer newLayer = layerService.createLayer(layerRequest);
        return ResponseEntity.ok(newLayer);
    }

}
