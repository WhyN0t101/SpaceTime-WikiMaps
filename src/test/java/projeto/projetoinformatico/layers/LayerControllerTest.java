package projeto.projetoinformatico.layers;

import com.github.jsonldjava.shaded.com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import projeto.projetoinformatico.controllers.LayerController;
import projeto.projetoinformatico.controllers.UserController;
import projeto.projetoinformatico.dtos.LayerDTO;
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.exceptions.Exception.InvalidParamsRequestException;
import projeto.projetoinformatico.exceptions.Exception.NotFoundException;
import projeto.projetoinformatico.exceptions.Exception.SparqlQueryException;
import projeto.projetoinformatico.model.SearchResult;
import projeto.projetoinformatico.service.LayerService;
import projeto.projetoinformatico.service.UserService;
import projeto.projetoinformatico.utils.Validation;

import javax.naming.directory.SearchControls;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class LayerControllerTest {

    private LayerService layerService;
    private Validation validation;
    private RateLimiter rateLimiter;
    private LayerController layerController;

    @BeforeEach
    public void setUp() {
        layerService = Mockito.mock(LayerService.class);
        validation = Mockito.mock(Validation.class);
        rateLimiter = Mockito.mock(RateLimiter.class);
        layerController = new LayerController(layerService, validation);
    }
    @Test
    public void testGetAllLayers_Success() {
        // Mock dependencies
        List<LayerDTO> layers = Arrays.asList(new LayerDTO(), new LayerDTO());


        // Set up mock behavior
        when(layerService.getAllLayers()).thenReturn(layers);

        // Call the endpoint
        ResponseEntity<List<LayerDTO>> response = layerController.getAllLayers();

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(layers, response.getBody());
    }

    @Test
    public void testGetLayerById_Success() {
        // Mock dependencies
        LayerDTO layerDTO = new LayerDTO();


        // Set up mock behavior
        when(layerService.getLayerById(1L)).thenReturn(layerDTO);

        // Call the endpoint
        ResponseEntity<LayerDTO> response = layerController.getLayerById(1L);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(layerDTO, response.getBody());
    }

    @Test
    public void testGetLayerById_LayerNotFound() {
        // Mock dependencies
        LayerDTO layerDTO = new LayerDTO();

        LayerController layerController = new LayerController(layerService, validation);

        // Set up mock behavior
        when(layerService.getLayerById(100L)).thenThrow(new NotFoundException("Layer not found with id: 100"));

        // Assert the response
        assertThrows(NotFoundException.class, () -> {
            layerController.getLayerById(100L);
        });
    }

    @Test//Falha na Verificação das coords no layerController.getLayerResultsByIdWithParams
    public void testGetLayerByIdWithParams_Success() {
        // Mock dependencies
        SearchResult searchResult = new SearchResult(List.of(Map.of("key", "value")));

        // Set up mock behavior
        //when(rateLimiter.tryAcquire()).thenReturn(true);
        //when(validation.isValidCoordinate(eq(38.7223), eq(-9.1393), eq(41.1579), eq(-8.6291))).thenReturn(true);
        when(layerService.getLayerByIdWithParams(2L, 38.7223, -9.1393, 41.1579, -8.6291,
                2000L, 2020L)).thenReturn(searchResult);

        // Call the endpoint
        ResponseEntity<?> response = layerController.getLayerResultsByIdWithParams(2L, 38.7223, -8.6291, 41.1579, -9.1393,
                2000L, 2020L);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(searchResult, response.getBody());
    }

    @Test//layerController.getLayerResultsByIdWithParams manda sempre invalid Params
    public void testGetLayerResultsByIdWithParams_TooManyRequests() {
        when(rateLimiter.tryAcquire()).thenReturn(false);

        ResponseEntity<?> response = layerController.getLayerResultsByIdWithParams(
                1L, 38.7223, -9.1393, 41.1579, -8.6291, 2000L, 2020L);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
    }

    @Test
    public void testGetLayerResultsByIdWithParams_InvalidParams() {
        when(rateLimiter.tryAcquire()).thenReturn(true);
        when(validation.isValidCoordinate(eq(38.7223), eq(-9.1393), eq(41.1579), eq(-8.6291))).thenReturn(false);

        try {
            layerController.getLayerResultsByIdWithParams(
                    1L, 38.7223, -9.1393, 41.1579, -8.6291, 2000L, 2020L);
        } catch (InvalidParamsRequestException e) {
            assertEquals("Invalid params", e.getMessage());
        }
    }

    @Test
    public void testGetLayerResultsByIdWithParams_SparqlQueryException() {
        when(rateLimiter.tryAcquire()).thenReturn(true);
        when(validation.isValidCoordinate(eq(38.7223), eq(-9.1393), eq(41.1579), eq(-8.6291))).thenReturn(true);

        when(layerService.getLayerByIdWithParams(any(Long.class), any(Double.class), any(Double.class), any(Double.class), any(Double.class), any(Long.class), any(Long.class)))
                .thenThrow(new SparqlQueryException("Invalid Sparql Query"));

        try {
            layerController.getLayerResultsByIdWithParams(
                    1L, 38.7223, -9.1393, 41.1579, -8.6291, 2000L, 2020L);
        } catch (SparqlQueryException e) {
            assertEquals("Invalid Sparql Query", e.getMessage());
        }
    }
}
