package projeto.projetoinformatico.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import projeto.projetoinformatico.dtos.LayerDTO;
import projeto.projetoinformatico.dtos.RoleUpgradeDTO;
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.exceptions.Exception.InvalidRequestException;
import projeto.projetoinformatico.exceptions.Exception.NotFoundException;
import projeto.projetoinformatico.model.SearchResult;
import projeto.projetoinformatico.model.layers.Layer;
import projeto.projetoinformatico.model.layers.LayersRepository;
import projeto.projetoinformatico.model.roleUpgrade.RoleUpgrade;
import projeto.projetoinformatico.model.users.User;
import projeto.projetoinformatico.model.users.UserRepository;
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

    private final ModelMapperUtils mapperUtils;
    private final UserRepository userRepository;

    @Autowired
    public LayerService(LayersRepository layersRepository, SparqlQueryProvider sparqlQueryProvider, SearchService searchService, ModelMapperUtils mapperUtils,UserRepository userRepository) {
        this.layersRepository = layersRepository;
        this.sparqlQueryProvider = sparqlQueryProvider;
        this.userRepository = userRepository;
        this.searchService = searchService;
        this.mapperUtils = mapperUtils;

    }


    @CacheEvict(value = "layerCache", allEntries = true)
    public LayerDTO createLayer(String username, LayerRequest layerRequest) {
        User user = userRepository.findByUsername(username);
        validateLayerRequest(layerRequest);
        checkDuplicateLayerName(layerRequest.getName());
        Layer newLayer = new Layer();
        newLayer.setUser(user);
        newLayer.setLayerName(layerRequest.getName());
        newLayer.setDescription(layerRequest.getDescription());
        newLayer.setQuery(layerRequest.getQuery());
        Layer savedLayer = saveLayer(newLayer);
        LayerDTO savedLayerDTO = convertLayerToDTO(savedLayer);
        savedLayerDTO.setUser(user);
        return savedLayerDTO;
    }

    public SearchResult getLayerByIdWithParams(Long id, Double lat1, Double lon1, Double lat2, Double lon2, Long start, Long end) {
        LayerDTO layer = getLayerById(id);
        String query = layer.getQuery();
        validateSparqlQuery(query);
        String filterQuery = sparqlQueryProvider.buildFilterQuery(query, lat1, lon1, lat2, lon2, start, end);
        return searchService.executeSparqlQuery(filterQuery);
    }


    @Cacheable(value = "layerCache")
    public List<LayerDTO> getAllLayers() {
        List<Layer> layers = layersRepository.findAll();
        return layers.stream()
                .map(this::convertLayerToDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "layerCache", key = "#id")
    public LayerDTO getLayerById(Long id) {
        Layer layer = layersRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Layer not found with id: " + id));
        return convertLayerToDTO(layer);
    }

    @CacheEvict(value = "layerCache", key = "#id")
    public LayerDTO updateLayer(Long id, LayerRequest layerRequest) {
        validateLayerRequest(layerRequest);
        checkDuplicateLayerName(layerRequest.getName());
        Layer existingLayer = layersRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Layer not found with id: " + id));;

        BeanUtils.copyProperties(layerRequest, existingLayer, "id");
        existingLayer.setTimestamp(new Date());
        Layer newLayer = saveLayer(existingLayer);
        newLayer.setLayerName(layerRequest.getName());
        return convertLayerToDTO(newLayer);
    }

    @CacheEvict(value = "layerCache", key = "#id")
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
        return !query.startsWith("SELECT DISTINCT ?item ?itemLabel ?description ?coordinates ?image ?itemSchemaLabel WHERE {\n")
                || !query.contains("SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE]\". }\n")
                || !query.contains("SELECT DISTINCT ?item ?itemLabel ?coordinates ?itemSchemaLabel WHERE {")
                || !query.contains("wdt:P625");
    }

    private void validateSparqlQuery(String query) {
        if (isSparqlQueryValid(query)) {
            throw new InvalidRequestException("Invalid SPARQL query");
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
    @Cacheable(value = "layerCache")
    public Page<LayerDTO> getAllLayersPaged(Pageable pageable) {
        Page<Layer> layers = layersRepository.findAll(pageable);
        return layers.map(this::convertLayerToDTO);
    }
    private LayerDTO convertLayerToDTO(Layer layer) {
        return mapperUtils.layerToDTO(layer, LayerDTO.class);
    }
}
