package projeto.projetoinformatico.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projeto.projetoinformatico.exceptions.Exception.InvalidRequestException;
import projeto.projetoinformatico.exceptions.Exception.NotFoundException;
import projeto.projetoinformatico.model.SearchResult;
import projeto.projetoinformatico.model.layers.Layer;
import projeto.projetoinformatico.model.layers.LayersRepository;
import projeto.projetoinformatico.requests.LayerRequest;
import projeto.projetoinformatico.utils.SparqlQueryProvider;

import java.util.Date;
import java.util.List;

@Service
public class LayerService {

    private final LayersRepository layersRepository;
    private final SparqlQueryProvider sparqlQueryProvider;
    private final SearchService searchService;

    @Autowired
    public LayerService(LayersRepository layersRepository, SparqlQueryProvider sparqlQueryProvider, SearchService searchService) {
        this.layersRepository = layersRepository;
        this.sparqlQueryProvider = sparqlQueryProvider;
        this.searchService = searchService;
    }

    public Layer createLayer(String username, LayerRequest layerRequest) {
        validateLayerRequest(layerRequest);
        checkDuplicateLayerName(layerRequest.getName());

        Layer newLayer = new Layer();
        newLayer.setUsername(username);
        newLayer.setLayerName(layerRequest.getName());
        newLayer.setDescription(layerRequest.getDescription());
        newLayer.setQuery(layerRequest.getQuery());

        return saveLayer(newLayer);
    }

    public SearchResult getLayerByIdWithParams(Long id, Double lat1, Double lon1, Double lat2, Double lon2, Long start, Long end) {
        Layer layer = getLayerById(id);
        String query = layer.getQuery();

        validateSparqlQuery(query);

        String filterQuery = sparqlQueryProvider.buildFilterQuery(query, lat1, lon1, lat2, lon2, start, end);

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
        Layer existingLayer = getLayerById(id);
        BeanUtils.copyProperties(layerRequest, existingLayer, "id");
        existingLayer.setTimestamp(new Date());
        return saveLayer(existingLayer);
    }

    public void deleteLayer(Long id) {
        if (id == null) {
            throw new InvalidRequestException("Layer ID cannot be null");
        }
        if (layersRepository.existsById(id)) {
            layersRepository.deleteById(id);
        } else {
            throw new NotFoundException("Layer not found with id: " + id);
        }
    }
    private Layer saveLayer(Layer layer) {
        return layersRepository.save(layer);
    }
    public List<Layer> findByKeywords(String query) {
        String lowercaseQuery = query.toLowerCase();
        return layersRepository.findByKeywords(lowercaseQuery);
    }

    private boolean isSparqlQueryValid(String query) {
        return !query.startsWith("SELECT DISTINCT ?item ?itemLabel ?coordinates WHERE {")
                || !query.contains("SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE]\". }")
                || !query.contains("SELECT DISTINCT ?item ?coordinates WHERE {")
                || !query.contains("wdt:P625");
    }

    private void validateSparqlQuery(String query) {
        if (isSparqlQueryValid(query)) {
            throw new InvalidRequestException("Invalid SPARQL query format");
        }
    }
    private void validateLayerRequest(LayerRequest layerRequest) {
        if (layerRequest == null || isSparqlQueryValid(layerRequest.getQuery())) {
            throw new InvalidRequestException("Invalid layer request");
        }
    }

    private void checkDuplicateLayerName(String name) {
        if (layersRepository.existsByLayerName(name)) {
            throw new InvalidRequestException("Layer name already exists: " + name);
        }
    }


}
