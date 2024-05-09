package projeto.projetoinformatico.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projeto.projetoinformatico.dtos.LayerDTO;
import projeto.projetoinformatico.exceptions.Exception.InvalidRequestException;
import projeto.projetoinformatico.exceptions.Exception.NotFoundException;
import projeto.projetoinformatico.model.SearchResult;
import projeto.projetoinformatico.model.layers.Layer;
import projeto.projetoinformatico.model.layers.LayersRepository;
import projeto.projetoinformatico.requests.LayerRequest;
import projeto.projetoinformatico.utils.ModelMapperUtils;
import projeto.projetoinformatico.utils.SparqlQueryProvider;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LayerService {

    private final LayersRepository layersRepository;
    private final SparqlQueryProvider sparqlQueryProvider;
    private final SearchService searchService;
    private final ModelMapperUtils modelMapperUtils;

    @Autowired
    public LayerService(LayersRepository layersRepository, SparqlQueryProvider sparqlQueryProvider, SearchService searchService,ModelMapperUtils modelMapperUtils) {
        this.layersRepository = layersRepository;
        this.sparqlQueryProvider = sparqlQueryProvider;
        this.searchService = searchService;
        this.modelMapperUtils = modelMapperUtils;

    }

    public LayerDTO createLayer(String username, LayerRequest layerRequest) {
        validateLayerRequest(layerRequest);
        checkDuplicateLayerName(layerRequest.getName());
        Layer newLayer = new Layer();
        newLayer.setUsername(username);
        newLayer.setLayerName(layerRequest.getName());
        newLayer.setDescription(layerRequest.getDescription());
        newLayer.setQuery(layerRequest.getQuery());
        Layer savedLayer = saveLayer(newLayer);
        return convertLayerToDTO(savedLayer);
    }

    public SearchResult getLayerByIdWithParams(Long id, Double lat1, Double lon1, Double lat2, Double lon2, Long start, Long end) {
        LayerDTO layer = getLayerById(id);
        String query = layer.getQuery();
        validateSparqlQuery(query);
        String filterQuery = sparqlQueryProvider.buildFilterQuery(query, lat1, lon1, lat2, lon2, start, end);
        return searchService.executeSparqlQuery(filterQuery);
    }

    public List<LayerDTO> getAllLayers() {
        List<Layer> layers = layersRepository.findAll();
        return layers.stream()
                .map(this::convertLayerToDTO)
                .collect(Collectors.toList());
    }

    public LayerDTO getLayerById(Long id) {
        Layer layer = layersRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Layer not found with id: " + id));
        return convertLayerToDTO(layer);
    }

    public LayerDTO updateLayer(Long id, LayerRequest layerRequest) {
        Layer existingLayer = layersRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Layer not found with id: " + id));;

        BeanUtils.copyProperties(layerRequest, existingLayer, "id");
        existingLayer.setTimestamp(new Date());
        Layer newLayer = saveLayer(existingLayer);

        return convertLayerToDTO(newLayer);
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

    public List<LayerDTO> findByKeywords(String query) {
        String lowercaseQuery = query.toLowerCase();
        List<Layer> layers = layersRepository.findByKeywords(lowercaseQuery);
        return layers.stream()
                .map(this::convertLayerToDTO)
                .collect(Collectors.toList());
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
    private LayerDTO convertLayerToDTO(Layer layer) {
        ModelMapper modelMapper = new ModelMapper();
        ModelMapperUtils mapperUtils = new ModelMapperUtils(modelMapper);
        return mapperUtils.layerToDTO(layer, LayerDTO.class);
    }

}
