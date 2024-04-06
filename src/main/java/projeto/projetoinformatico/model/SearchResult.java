package projeto.projetoinformatico.model;

import java.util.List;
import java.util.Map;

public class SearchResult {
    private final List<Map<String, String>> results;

    public SearchResult(List<Map<String, String>> results) {
        this.results = results;
    }

    public List<Map<String, String>> getResults() {
        return results;
    }

}
