package projeto.projetoinformatico.layers;

import com.github.jsonldjava.shaded.com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import projeto.projetoinformatico.controllers.LayerController;
import projeto.projetoinformatico.controllers.UserController;
import projeto.projetoinformatico.dtos.LayerDTO;
import projeto.projetoinformatico.dtos.Paged.LayerPageDTO;
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.exceptions.Exception.InvalidParamsRequestException;
import projeto.projetoinformatico.exceptions.Exception.InvalidRequestException;
import projeto.projetoinformatico.exceptions.Exception.NotFoundException;
import projeto.projetoinformatico.exceptions.Exception.SparqlQueryException;
import projeto.projetoinformatico.model.SearchResult;
import projeto.projetoinformatico.requests.LayerRequest;
import projeto.projetoinformatico.service.LayerService;
import projeto.projetoinformatico.service.UserService;
import projeto.projetoinformatico.utils.Validation;

import javax.naming.directory.SearchControls;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LayerControllerTest {

    private MockMvc mockMvc;

    private LayerService layerService;
    private Validation validation;
    private RateLimiter rateLimiter;
    private LayerController layerController;

    @BeforeEach
    public void setUp() {
        layerService = mock(LayerService.class);
        validation = mock(Validation.class);
        rateLimiter = mock(RateLimiter.class);
        layerController = new LayerController(layerService, validation);
    }

    @Test
    public void testGetAllLayers_Success_NoQuery() {
        Pageable pageable = PageRequest.of(0, 10);
        List<LayerDTO> layerDTOList = List.of(new LayerDTO(), new LayerDTO());
        Page<LayerDTO> layers = new PageImpl<>(layerDTOList, pageable, layerDTOList.size());

        when(layerService.getAllLayersPaged(pageable)).thenReturn(layers);

        ResponseEntity<LayerPageDTO> response = layerController.getAllLayers(0, 10, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(layerDTOList, response.getBody().getLayers());
        assertEquals(0, response.getBody().getCurrentPage());
        assertEquals(layerDTOList.size(), response.getBody().getTotalItems());
        assertEquals(1, response.getBody().getTotalPages());
    }

    @Test
    public void testGetAllLayers_Success_WithQuery() {
        Pageable pageable = PageRequest.of(0, 10);
        List<LayerDTO> layerDTOList = List.of(new LayerDTO(), new LayerDTO());
        Page<LayerDTO> layers = new PageImpl<>(layerDTOList, pageable, layerDTOList.size());

        when(layerService.findByKeywordsPaged("keyword", pageable)).thenReturn(layers);

        ResponseEntity<LayerPageDTO> response = layerController.getAllLayers(0, 10, "keyword");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(layerDTOList, response.getBody().getLayers());
        assertEquals(0, response.getBody().getCurrentPage());
        assertEquals(layerDTOList.size(), response.getBody().getTotalItems());
        assertEquals(1, response.getBody().getTotalPages());
    }

    @Test
    public void testGetAllLayers_InvalidSize() {
        try {
            layerController.getAllLayers(0, 0, null);
        } catch (InvalidParamsRequestException e) {
            assertEquals("Invalid size of pagination", e.getMessage());
        }
    }

    @Test
    public void testGetAllLayers_InvalidPage() {
        try {
            layerController.getAllLayers(-1, 10, null);
        } catch (InvalidParamsRequestException e) {
            assertEquals("Invalid page of pagination", e.getMessage());
        }
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
    public void testRateLimiter() throws Exception {
        when(validation.isValidCoordinate(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble())).thenReturn(true);

        // Send requests more than the rate limit within a short period
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/layers/1")
                            .param("lat1", "38")
                            .param("lon1", "-9")
                            .param("lat2", "41")
                            .param("lon2", "-8")
                            .param("start", "2000")
                            .param("end", "2020"))
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }

        // The next request should be rate limited
        mockMvc.perform(MockMvcRequestBuilders.get("/api/layers/1")
                        .param("lat1", "38")
                        .param("lon1", "-9")
                        .param("lat2", "41")
                        .param("lon2", "-8")
                        .param("start", "2000")
                        .param("end", "2020"))
                .andExpect(MockMvcResultMatchers.status().isTooManyRequests());
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

    @Test
    public void testCreateLayer_Success() {
        // Mock SecurityContext and Authentication
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);

        // Mock authentication behavior
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");

        // Prepare the LayerRequest
        LayerRequest layerRequest = new LayerRequest();
        layerRequest.setName("Test Layer");
        layerRequest.setDescription("Test Description");
        layerRequest.setSparqlQuery("SELECT * WHERE { ?s ?p ?o }");

        // Prepare the LayerDTO
        LayerDTO layerDTO = new LayerDTO();
        layerDTO.setLayerName("Test Layer");
        layerDTO.setDescription("Test Description");
        layerDTO.setQuery("SELECT * WHERE { ?s ?p ?o }");

        // Mock the LayerService createLayer method
        when(layerService.createLayer(any(String.class), any(LayerRequest.class))).thenReturn(layerDTO);

        // Call the controller method
        ResponseEntity<LayerDTO> response = layerController.createLayer(layerRequest);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(layerDTO, response.getBody());
    }

    @Test
    public void testCreateLayer_InvalidRequestException() {
        // Mock SecurityContext and Authentication
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);

        // Mock authentication behavior
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");

        // Prepare the LayerRequest
        LayerRequest layerRequest = new LayerRequest();
        layerRequest.setName("Test Layer");
        layerRequest.setDescription("Test Description");
        layerRequest.setSparqlQuery("SELECT * WHERE { ?s ?p ?o }");

        // Mock the LayerService createLayer method to throw InvalidRequestException
        when(layerService.createLayer(any(String.class), any(LayerRequest.class)))
                .thenThrow(new InvalidRequestException("Invalid layer request"));

        // Call the controller method and handle the exception
        ResponseEntity<LayerDTO> response = layerController.createLayer(layerRequest);

        // Assert the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testCreateLayer_GeneralException() {
        // Mock SecurityContext and Authentication
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);

        // Mock authentication behavior
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");

        // Prepare the LayerRequest
        LayerRequest layerRequest = new LayerRequest();
        layerRequest.setName("Test Layer");
        layerRequest.setDescription("Test Description");
        layerRequest.setSparqlQuery("SELECT * WHERE { ?s ?p ?o }");

        // Mock the LayerService createLayer method to throw a general exception
        when(layerService.createLayer(any(String.class), any(LayerRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Call the controller method and handle the exception
        ResponseEntity<LayerDTO> response = layerController.createLayer(layerRequest);

        // Assert the response
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testUpdateLayer_Success() {
        // Prepare the LayerRequest
        LayerRequest layerRequest = new LayerRequest();
        layerRequest.setName("Updated Layer");
        layerRequest.setDescription("Updated Description");
        layerRequest.setSparqlQuery("SELECT * WHERE { ?s ?p ?o }");

        // Prepare the LayerDTO
        LayerDTO layerDTO = new LayerDTO();
        layerDTO.setLayerName("Updated Layer");
        layerDTO.setDescription("Updated Description");
        layerDTO.setQuery("SELECT * WHERE { ?s ?p ?o }");

        // Mock the LayerService updateLayer method
        when(layerService.updateLayer(eq(1L), any(LayerRequest.class))).thenReturn(layerDTO);

        // Call the controller method
        ResponseEntity<LayerDTO> response = layerController.updateLayer(1L, layerRequest);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(layerDTO, response.getBody());
    }

    @Test
    public void testUpdateLayer_NotFoundException() {
        // Prepare the LayerRequest
        LayerRequest layerRequest = new LayerRequest();
        layerRequest.setName("Updated Layer");
        layerRequest.setDescription("Updated Description");
        layerRequest.setSparqlQuery("SELECT * WHERE { ?s ?p ?o }");

        // Mock the LayerService updateLayer method to throw NotFoundException
        when(layerService.updateLayer(eq(1L), any(LayerRequest.class)))
                .thenThrow(new NotFoundException("Layer not found with id: " + 1000L));

        // Call the controller method and handle the exception
        ResponseEntity<LayerDTO> response = layerController.updateLayer(1000L, layerRequest);

        // Assert the response
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testUpdateLayer_GeneralException() {
        // Prepare the LayerRequest
        LayerRequest layerRequest = new LayerRequest();
        layerRequest.setName("Updated Layer");
        layerRequest.setDescription("Updated Description");
        layerRequest.setSparqlQuery("SELECT * WHERE { ?s ?p ?o }");

        // Mock the LayerService updateLayer method to throw a general exception
        when(layerService.updateLayer(eq(1L), any(LayerRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Call the controller method and handle the exception
        ResponseEntity<LayerDTO> response = layerController.updateLayer(1L, layerRequest);

        // Assert the response
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testDeleteLayer_Success() {
        // Mock SecurityContext and Authentication
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);

        // Mock authentication behavior
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");

        // Mock the LayerService deleteLayer method
        layerController.deleteLayer(1L);

        // Assert the response
        ResponseEntity<Void> response = layerController.deleteLayer(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testDeleteLayer_InvalidRequestException() {
        // Mock the LayerService deleteLayer method to throw InvalidRequestException
        Mockito.doThrow(new InvalidRequestException("Layer ID cannot be null"))
                .when(layerService).deleteLayer(null);

        // Assert the response
        ResponseEntity<Void> response = layerController.deleteLayer(null);
        assertThrows(InvalidRequestException.class, () -> {
            layerController.deleteLayer(null);
        });
    }

    @Test
    public void testDeleteLayer_NotFoundException() {
        // Mock the LayerService deleteLayer method to throw NotFoundException
        Mockito.doThrow(new NotFoundException("Layer not found with id: 1"))
                .when(layerService).deleteLayer(1L);

        // Assert the response
        ResponseEntity<Void> response = layerController.deleteLayer(1L);
        assertThrows(NotFoundException.class, () -> {
            layerController.deleteLayer(1L);
        });
    }


}
