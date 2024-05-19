package projeto.projetoinformatico.dtos.Paged;

import lombok.Data;
import projeto.projetoinformatico.dtos.LayerDTO;

import java.util.List;

@Data
public class LayerPageDTO {
    private List<LayerDTO> layers;
    private int currentPage;
    private int totalItems;
    private int totalPages;

    public LayerPageDTO(List<LayerDTO> layers, int currentPage, int totalItems, int totalPages) {
        this.layers = layers;
        this.currentPage = currentPage;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
    }
}