package projeto.projetoinformatico.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projeto.projetoinformatico.model.SearchResult;
import projeto.projetoinformatico.model.layers.Layer;
import projeto.projetoinformatico.model.layers.LayersRepository;
import projeto.projetoinformatico.requests.LayerRequest;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class LayerService {

    private final LayersRepository layersRepository;
    private final SearchService searchService;

    @Autowired
    public LayerService(LayersRepository layersRepository, SearchService searchService) {
        this.layersRepository = layersRepository;
        this.searchService = searchService;
    }

    public Layer createLayer(String username, LayerRequest layerRequest) {
        if (layerRequest == null) {
            throw new IllegalArgumentException("Layer request cannot be null");
        }

        Layer newLayer = new Layer();
        newLayer.setUsername(username);
        newLayer.setLayerName(layerRequest.getName());
        newLayer.setDescription(layerRequest.getDescription());
        newLayer.setQuery(layerRequest.getQuery());

        return getLayer(layerRequest, newLayer);
    }

    public List<Layer> getAllLayers() {
        return layersRepository.findAll();
    }

    public Layer getLayerById(Long id) {
        return layersRepository.findById(id).orElse(null);
    }

    public Layer updateLayer(Long id, LayerRequest layerRequest) {
        Optional<Layer> optionalLayer = layersRepository.findById(id);
        if (optionalLayer.isPresent()) {
            Layer existingLayer = optionalLayer.get();
            BeanUtils.copyProperties(layerRequest, existingLayer, "id");
            existingLayer.setTimestamp(new Date());
            return getLayer(layerRequest, existingLayer);
        }
        return null;
    }

    private Layer getLayer(LayerRequest layerRequest, Layer existingLayer) {
        SearchResult searchResult = searchService.executeSparqlQuery(layerRequest.getQuery());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String searchResultJson = objectMapper.writeValueAsString(searchResult);
            existingLayer.setSearchResult(searchResultJson);
        } catch (JsonProcessingException e) {
            // Handle the exception
            e.printStackTrace();
        }
        return layersRepository.save(existingLayer);
    }

    public boolean deleteLayer(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Layer ID cannot be null");
        }
        if (layersRepository.existsById(id)) {
            layersRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
