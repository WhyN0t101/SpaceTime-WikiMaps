package projeto.projetoinformatico.requests;

import java.util.List;
import java.util.Map;

public class LayerRequest {
    private String name;
    private String description;
    private String query;
    private List<Map<String, String>> results;

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

    public List<Map<String, String>> getResults() {
        return results;
    }

    public void setResults(List<Map<String, String>> results) {
        this.results = results;
    }
}
