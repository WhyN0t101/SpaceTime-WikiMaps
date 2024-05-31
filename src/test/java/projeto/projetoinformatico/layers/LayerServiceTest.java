package projeto.projetoinformatico.layers;

import com.github.jsonldjava.shaded.com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import projeto.projetoinformatico.controllers.LayerController;
import projeto.projetoinformatico.dtos.LayerDTO;
import projeto.projetoinformatico.exceptions.Exception.InvalidRequestException;
import projeto.projetoinformatico.exceptions.Exception.NotFoundException;
import projeto.projetoinformatico.model.SearchResult;
import projeto.projetoinformatico.model.layers.Layer;
import projeto.projetoinformatico.model.layers.LayersRepository;
import projeto.projetoinformatico.model.users.Role;
import projeto.projetoinformatico.model.users.User;
import projeto.projetoinformatico.model.users.UserRepository;
import projeto.projetoinformatico.requests.LayerRequest;
import projeto.projetoinformatico.service.LayerService;
import projeto.projetoinformatico.service.SearchService;
import projeto.projetoinformatico.utils.ModelMapperUtils;
import projeto.projetoinformatico.utils.SparqlQueryProvider;
import projeto.projetoinformatico.utils.Validation;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class LayerServiceTest {


    private LayerService layerService;

    private LayersRepository layersRepository;

    private SparqlQueryProvider sparqlQueryProvider;

    private SearchService searchService;

    private ModelMapperUtils mapperUtils;

    private UserRepository userRepository;


    @BeforeEach
    public void setUp() {
        sparqlQueryProvider = mock(SparqlQueryProvider.class);
        mapperUtils = mock(ModelMapperUtils.class);
        searchService = mock(SearchService.class);
        userRepository = mock(UserRepository.class);
        layersRepository = mock(LayersRepository.class);
        layerService = new LayerService(layersRepository, sparqlQueryProvider, searchService
                , mapperUtils, userRepository);

    }

    @Test
    void createLayer_Success() {
        // Arrange
        String username = "testuser";

        LayerRequest layerRequest = new LayerRequest();
        layerRequest.setName("Test Layer");
        layerRequest.setDescription("Test Description");
        layerRequest.setSparqlQuery("Test Query");

        User user = new User();
        user.setUsername(username);
        user.setRole(Role.USER);

        when(userRepository.findByUsername(username)).thenReturn(user);
        when(layersRepository.existsByLayerName(layerRequest.getName())).thenReturn(false);
        Layer savedLayer = new Layer();
        savedLayer.setId(1L);

        when(layersRepository.save(any(Layer.class))).thenReturn(savedLayer);
        LayerDTO expectedDTO = new LayerDTO();
        expectedDTO.setId(1L);
        expectedDTO.setUser(user);
        when(mapperUtils.layerToDTO(savedLayer, LayerDTO.class)).thenReturn(expectedDTO);

        // Act
        LayerDTO createdLayer = layerService.createLayer(username, layerRequest);

        // Assert
        assertNotNull(createdLayer);
        assertEquals(expectedDTO.getId(), createdLayer.getId());
        assertEquals(expectedDTO.getUserDTO(), createdLayer.getUserDTO());
    }

    @Test
    void createLayer_InvalidRequest() {
        // Arrange
        String username = "testuser";
        LayerRequest layerRequest = new LayerRequest();
        layerRequest.setName("Test Layer");
        layerRequest.setDescription("Test Description");
        layerRequest.setSparqlQuery("Invalid Query"); // Set the query here instead of sparqlQuery
        when(userRepository.findByUsername(username)).thenReturn(new User());
        when(sparqlQueryProvider.isSparqlQueryValid(anyString())).thenReturn(true); // Mock the behavior of the sparqlQueryProvider

        // Act & Assert
        assertThrows(InvalidRequestException.class, () -> layerService.createLayer(username, layerRequest));
    }

    @Test
    void createLayer_DuplicateLayerName() {
        // Arrange
        String username = "testuser";
        LayerRequest layerRequest = new LayerRequest();
        layerRequest.setName("Test Layer");
        layerRequest.setDescription("Test Description");
        layerRequest.setSparqlQuery("Valid Query");
        when(userRepository.findByUsername(username)).thenReturn(new User());
        when(layersRepository.existsByLayerName(layerRequest.getName())).thenReturn(true);

        // Act & Assert
        assertThrows(InvalidRequestException.class, () -> layerService.createLayer(username, layerRequest));
    }

    @Test
    void getLayerResultsByIdWithParams_Success() {
        // Mock parameters
        Long id = 1L;
        Double lat1 = 1.0;
        Double lon2 = 2.0;
        Double lat2 = 3.0;
        Double lon1 = 4.0;
        Long start = 1000L;
        Long end = 2000L;

        // Mock layerDTO
        LayerDTO layerDTO = new LayerDTO();
        layerDTO.setId(id);
        layerDTO.setQuery("Valid Query");

        // Mock layersRepository behavior
        when(layersRepository.findById(id)).thenReturn(Optional.of(new Layer())); // Assuming findById returns Optional
        when(mapperUtils.layerToDTO(any(Layer.class), eq(LayerDTO.class))).thenReturn(layerDTO);

        // Mock layer service response
        SearchResult mockSearchResult = new SearchResult(Collections.emptyList());
        when(layerService.getLayerByIdWithParams(id, lat1, lon1, lat2, lon2, start, end)).thenReturn(mockSearchResult);

        // Call the service method
        SearchResult result = layerService.getLayerByIdWithParams(id, lat1, lon1, lat2, lon2, start, end);

        // Verify result
        assertEquals(mockSearchResult, result);
    }

    @Test
    void getLayerByIdWithParams_LayerNotFound() {
        // Mock parameters
        Long id = 1L;
        Double lat1 = 1.0;
        Double lon2 = 2.0;
        Double lat2 = 3.0;
        Double lon1 = 4.0;
        Long start = 1000L;
        Long end = 2000L;

        // Mock layerDTO
        LayerDTO layerDTO = new LayerDTO();
        layerDTO.setId(id);
        layerDTO.setQuery("Valid Query");

        // Mock layersRepository behavior
        when(layersRepository.findById(id)).thenReturn(Optional.of(new Layer())); // Assuming findById returns Optional
        when(mapperUtils.layerToDTO(any(Layer.class), eq(LayerDTO.class))).thenReturn(layerDTO);

        // Mock layer service response
        SearchResult mockSearchResult = new SearchResult(Collections.emptyList());
        when(layerService.getLayerByIdWithParams(id, lat1, lon1, lat2, lon2, start, end)).thenThrow(new NotFoundException("Layer not found with id: " + id));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> layerService.getLayerByIdWithParams(id, 0.0, 0.0, 0.0, 0.0, 0L, 0L));
    }

    @Test
    void getLayerByIdWithParams_InvalidSparqlQuery() {
        // Mock parameters
        Long id = 1L;
        Double lat1 = 1.0;
        Double lon2 = 2.0;
        Double lat2 = 3.0;
        Double lon1 = 4.0;
        Long start = 1000L;
        Long end = 2000L;

        // Mock layerDTO
        LayerDTO layerDTO = new LayerDTO();
        layerDTO.setId(id);
        layerDTO.setQuery("Valid Query");

        // Mock layersRepository behavior
        when(layersRepository.findById(id)).thenReturn(Optional.of(new Layer())); // Assuming findById returns Optional
        when(mapperUtils.layerToDTO(any(Layer.class), eq(LayerDTO.class))).thenReturn(layerDTO);

        // Mock layer service response
        SearchResult mockSearchResult = new SearchResult(Collections.emptyList());
        when(layerService.getLayerByIdWithParams(id, lat1, lon1, lat2, lon2, start, end)).thenThrow(new InvalidRequestException("Invalid SPARQL query"));

        // Mocking isSparqlQueryValid to return true for invalid query
        //when(sparqlQueryProvider.isSparqlQueryValid("Invalid Query")).thenReturn(true);

        // Act & Assert
        assertThrows(InvalidRequestException.class, () ->
                layerService.getLayerByIdWithParams(id, 0.0, 0.0, 0.0, 0.0, 0L, 0L)
        );
    }

    @Test
    void getLayerById_Success() {
        // Arrange
        Long id = 1L;
        Layer layer = new Layer(); // Create a new Layer object
        LayerDTO expectedDTO = new LayerDTO(); // Create a new LayerDTO object
        when(layersRepository.findById(id)).thenReturn(java.util.Optional.of(layer)); // Mocking repository behavior
        when(mapperUtils.layerToDTO(layer, LayerDTO.class)).thenReturn(expectedDTO); // Mocking mapper behavior

        // Act
        LayerDTO resultDTO = layerService.getLayerById(id);

        // Assert
        assertEquals(expectedDTO, resultDTO); // Check if the returned DTO is the expected one
    }

    @Test
    void getLayerById_LayerNotFound() {
        // Arrange
        Long id = 1L;
        when(layersRepository.findById(id)).thenReturn(java.util.Optional.empty()); // Mocking repository behavior

        // Act & Assert
        assertThrows(NotFoundException.class, () -> layerService.getLayerById(id)); // Check if NotFoundException is thrown
    }

    @Test
    void updateLayer_Success() {
        // Arrange
        Long id = 1L;
        LayerRequest layerRequest = new LayerRequest();
        layerRequest.setName("Test Layer");
        layerRequest.setDescription("Test Description");
        layerRequest.setSparqlQuery("Test Query");

        Layer existingLayer = new Layer();
        existingLayer.setId(id);
        existingLayer.setLayerName("Existing Layer");
        existingLayer.setDescription("Existing Description");
        existingLayer.setQuery("Existing Query");

        Layer updatedLayer = new Layer();
        updatedLayer.setId(id);
        updatedLayer.setLayerName(layerRequest.getName());
        updatedLayer.setDescription(layerRequest.getDescription());
        updatedLayer.setQuery(layerRequest.getQuery());
        updatedLayer.setTimestamp(new Date());

        when(layersRepository.findById(id)).thenReturn(Optional.of(existingLayer));
        when(layersRepository.existsByLayerName(layerRequest.getName())).thenReturn(false);
        when(layersRepository.save(existingLayer)).thenReturn(updatedLayer);

        LayerDTO expectedDTO = new LayerDTO();
        expectedDTO.setId(id);
        expectedDTO.setLayerName(layerRequest.getName());
        expectedDTO.setDescription(layerRequest.getDescription());
        expectedDTO.setQuery(layerRequest.getQuery());
        expectedDTO.setTimestamp(updatedLayer.getTimestamp());

        when(mapperUtils.layerToDTO(updatedLayer, LayerDTO.class)).thenReturn(expectedDTO);

        // Act
        LayerDTO resultDTO = layerService.updateLayer(id, layerRequest);

        // Assert
        assertNotNull(resultDTO);
        assertEquals(expectedDTO, resultDTO);
    }

    @Test
    void updateLayer_LayerNotFound() {
        // Arrange
        Long id = 1L;
        LayerRequest layerRequest = new LayerRequest();
        layerRequest.setName("Test Layer");
        layerRequest.setDescription("Test Description");
        layerRequest.setSparqlQuery("Test Query");

        when(layersRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> layerService.updateLayer(id, layerRequest));
    }

    @Test
    void updateLayer_DuplicateLayerName() {
        // Arrange
        Long id = 1L;
        LayerRequest layerRequest = new LayerRequest();
        layerRequest.setName("Test Layer");
        layerRequest.setDescription("Test Description");
        layerRequest.setSparqlQuery("Test Query");

        Layer existingLayer = new Layer();
        existingLayer.setId(id);
        existingLayer.setLayerName(layerRequest.getName());

        when(layersRepository.findById(id)).thenReturn(Optional.of(existingLayer));
        when(layersRepository.existsByLayerName(layerRequest.getName())).thenReturn(true);

        // Act & Assert
        assertThrows(InvalidRequestException.class, () -> layerService.updateLayer(id, layerRequest));
    }

    @Test
    void updateLayer_InvalidLayerRequest() {
        // Arrange
        Long id = 1L;
        LayerRequest layerRequest = new LayerRequest();
        layerRequest.setName("Test Layer");
        layerRequest.setDescription("Test Description");
        layerRequest.setSparqlQuery("Invalid Query");

        when(layersRepository.findById(id)).thenReturn(Optional.of(new Layer())); // Mock layer retrieval
        when(sparqlQueryProvider.isSparqlQueryValid("Invalid Query")).thenReturn(true); // Mock invalid SPARQL query

        // Act & Assert
        assertThrows(InvalidRequestException.class, () -> layerService.updateLayer(id, layerRequest));
        verify(layersRepository, never()).save(any()); // Ensure that the repository save method is not called
    }


}
