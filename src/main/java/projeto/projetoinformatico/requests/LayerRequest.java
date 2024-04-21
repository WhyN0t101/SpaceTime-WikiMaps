package projeto.projetoinformatico.requests;

import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Map;

public class LayerRequest {
    @NotBlank(message = "Query cannot be blank")
    private String name;
    private String description;

    @NotBlank(message = "Query cannot be blank")
    private String query;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuery() {
        return query;
    }

    public void setSparqlQuery(String query) {
        this.query = query;
    }


}
