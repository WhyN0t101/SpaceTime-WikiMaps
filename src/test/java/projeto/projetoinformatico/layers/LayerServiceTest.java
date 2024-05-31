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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        layerService = new LayerService(layersRepository, sparqlQueryProvider,searchService
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


}
