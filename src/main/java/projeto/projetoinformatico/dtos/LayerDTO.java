package projeto.projetoinformatico.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class LayerDTO {
    private Long id;
    private String username;
    private String layerName;
    private String description;
    private Date timestamp;
    private String query;

}