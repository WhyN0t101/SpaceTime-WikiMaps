package projeto.projetoinformatico.service;

import org.springframework.stereotype.Service;
import projeto.projetoinformatico.model.layers.Layer;
import projeto.projetoinformatico.requests.LayerRequest;

import java.util.List;

@Service
public class LayerService {
    public List<Record> getLayerRecords(Long id, String location, String date_start, String date_end) {
        return null;
    }

    public List<Layer> getAllLayers() {
        return null;
    }

    public Layer createLayer(LayerRequest layerRequest) {
        return null;
    }
}