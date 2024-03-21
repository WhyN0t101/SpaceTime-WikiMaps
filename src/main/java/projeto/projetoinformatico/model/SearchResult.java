package projeto.projetoinformatico.model;

import java.util.List;
import java.util.Map;

public class SearchResult {
    private List<Map<String, String>> itemLabels;

    public SearchResult(List<Map<String, String>> itemLabels) {
        this.itemLabels = itemLabels;
    }

    public List<Map<String, String>> getItemLabels() {
        return itemLabels;
    }

    public void setItemLabels(List<Map<String, String>> itemLabels) {
        this.itemLabels = itemLabels;
    }
}
