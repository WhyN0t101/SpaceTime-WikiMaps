package projeto.projetoinformatico.layers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import projeto.projetoinformatico.controllers.LayerController;
import projeto.projetoinformatico.controllers.UserController;
import projeto.projetoinformatico.dtos.LayerDTO;
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.exceptions.Exception.NotFoundException;
import projeto.projetoinformatico.service.LayerService;
import projeto.projetoinformatico.service.UserService;
import projeto.projetoinformatico.utils.Validation;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class LayerControllerTest {

    @Test
    public void testGetAllLayers_Success() {
        // Mock dependencies
        LayerService layerService = Mockito.mock(LayerService.class);
        Validation validation = Mockito.mock(Validation.class);
        LayerController layerController = new LayerController(layerService, validation);
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
        LayerService layerService = Mockito.mock(LayerService.class);
        Validation validation = Mockito.mock(Validation.class);
        LayerController layerController = new LayerController(layerService, validation);
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
        LayerService layerService = Mockito.mock(LayerService.class);
        Validation validation = Mockito.mock(Validation.class);
        LayerController layerController = new LayerController(layerService, validation);
        LayerDTO layerDTO = new LayerDTO();

        // Set up mock behavior
        when(layerService.getLayerById(100L)).thenThrow(new NotFoundException("Layer not found with id: 100"));

        // Assert the response
        assertThrows(NotFoundException.class, () -> {
            layerController.getLayerById(100L);
        });
    }

    @Test
    public void testGetLayerByIdWithParams_Success() {
        // Mock dependencies
        LayerService layerService = Mockito.mock(LayerService.class);
        Validation validation = Mockito.mock(Validation.class);
        LayerController layerController = new LayerController(layerService, validation);
        LayerDTO layerDTO = new LayerDTO();


        // Set up mock behavior
        when(layerService.getLayerById(1L)).thenReturn(layerDTO);

        // Call the endpoint
        ResponseEntity<?> response = layerController.getLayerById(1L);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(layerDTO, response.getBody());
    }
}
