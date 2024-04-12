package projeto.projetoinformatico.service;// LayerService.java

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projeto.projetoinformatico.model.SearchResult;
import projeto.projetoinformatico.model.layers.Layer;
import projeto.projetoinformatico.model.layers.LayersRepository;
import projeto.projetoinformatico.requests.LayerRequest;

import java.util.Date;
import java.util.List;

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
        newLayer.setDescription(layerRequest.getDescription());
        newLayer.setTimestamp(new Date());
        newLayer.setQuery(layerRequest.getQuery());
        // Execute the SPARQL query and get the results
        SearchResult searchResult = searchService.executeSparqlQuery(layerRequest.getQuery());

        // Convert the search result to JSON string and set it to the layer
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String searchResultJson = objectMapper.writeValueAsString(searchResult);
            newLayer.setSearchResult(searchResultJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // Handle the exception as needed
        }

        return layersRepository.save(newLayer);
    }
    public List<Layer> getAllLayers() {
        return layersRepository.findAll();
    }

    public Layer getLayerById(Long id) {
        return layersRepository.findById(id).orElse(null);
    }


}
