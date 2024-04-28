package projeto.projetoinformatico.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import projeto.projetoinformatico.exceptions.Exception.InvalidLayerRequestException;
import projeto.projetoinformatico.exceptions.Exception.NotFoundException;
import projeto.projetoinformatico.model.SearchResult;
import projeto.projetoinformatico.model.layers.Layer;
import projeto.projetoinformatico.model.layers.LayersRepository;
import projeto.projetoinformatico.requests.LayerRequest;
import projeto.projetoinformatico.utils.SparqlQueryProvider;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class LayerService {

    private final LayersRepository layersRepository;
    private final SparqlQueryProvider sparqlQueryProvider;

    private final SearchService searchService;

    @Autowired
    public LayerService(LayersRepository layersRepository,SparqlQueryProvider sparqlQueryProvider,SearchService searchService) {
        this.layersRepository = layersRepository;
        this.searchService = searchService;
        this.sparqlQueryProvider = sparqlQueryProvider;
    }

    public Layer createLayer(String username, LayerRequest layerRequest) {
        if (layerRequest == null) {
            throw new InvalidLayerRequestException("Layer request cannot be null");
        }

        Layer newLayer = new Layer();
        newLayer.setUsername(username);
        newLayer.setLayerName(layerRequest.getName());
        newLayer.setDescription(layerRequest.getDescription());
        newLayer.setQuery(layerRequest.getQuery());

        return saveLayer(newLayer);
    }

    //@Cacheable(value = "searchCache", key = "{ #id,#lat1, #lat2, #lon1, #lon2, #start, #end}")
    public SearchResult getLayerByIdWithParams(Long id, Double lat1, Double lon1, Double lat2, Double lon2, Long start, Long end) {
        Layer layer = getLayerById(id); // Retrieve layer by ID
        String query = layer.getQuery(); // Get the SPARQL query from the layer
        String filterQuery = sparqlQueryProvider.buildFilterQuery(query, lat1, lon1, lat2, lon2, start, end); // Build the filtered query
        return searchService.executeSparqlQuery(filterQuery);
    }
    public List<Layer> getAllLayers() {
        return layersRepository.findAll();
    }

    public Layer getLayerById(Long id) {
        return layersRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Layer not found with id: " + id));
    }

    public Layer updateLayer(Long id, LayerRequest layerRequest) {
        Optional<Layer> optionalLayer = layersRepository.findById(id);
        if (optionalLayer.isPresent()) {
            Layer existingLayer = optionalLayer.get();
            BeanUtils.copyProperties(layerRequest, existingLayer, "id");
            existingLayer.setTimestamp(new Date());
            return saveLayer(existingLayer);
        } else {
            throw new NotFoundException("Layer not found with id: " + id);
        }
    }

    private Layer saveLayer(Layer existingLayer) {
        return layersRepository.save(existingLayer);
    }

    public void deleteLayer(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Layer ID cannot be null");
        }
        if (layersRepository.existsById(id)) {
            layersRepository.deleteById(id);
        } else {
            throw new NotFoundException("Layer not found with id: " + id);
        }
    }
}
