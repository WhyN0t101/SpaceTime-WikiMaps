package projeto.projetoinformatico.service;// LayerService.java

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
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
        Layer newLayer = new Layer();
        newLayer.setUsername(username);
        newLayer.setLayerName(layerRequest.getName());
        newLayer.setDescription(layerRequest.getDescription());
        newLayer.setQuery(layerRequest.getQuery());
        // Execute the SPARQL query and get the results
        return getLayer(layerRequest, newLayer);
    }
    public List<Layer> getAllLayers() {
        return layersRepository.findAll();
    }

    public Layer getLayerById(Long id) {
        return layersRepository.findById(id).orElse(null);
    }


    // TO CHECK
    public Layer updateLayer(Long id, LayerRequest layerRequest) {
        Optional<Layer> optionalLayer = layersRepository.findById(id);
        if (optionalLayer.isPresent()) {
            Layer existingLayer = optionalLayer.get();
            // Update the existing layer with the new data
            BeanUtils.copyProperties(layerRequest, existingLayer, "id");
            existingLayer.setTimestamp(new Date());
            // Execute the updated SPARQL query and update the search result
            return getLayer(layerRequest, existingLayer);
        }
        return null; // or throw exception if needed
    }

    @NotNull
    private Layer getLayer(LayerRequest layerRequest, Layer existingLayer) {
        SearchResult searchResult = searchService.executeSparqlQuery(layerRequest.getQuery());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String searchResultJson = objectMapper.writeValueAsString(searchResult);
            existingLayer.setSearchResult(searchResultJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // Handle the exception as needed
        }
        return layersRepository.save(existingLayer);
    }

    public boolean deleteLayer(Long id) {
        if (layersRepository.existsById(id)) {
            layersRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
