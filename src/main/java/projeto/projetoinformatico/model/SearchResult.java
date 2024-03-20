package projeto.projetoinformatico.model;

import java.util.List;

public class SearchResult {
    private List<String> itemLabels;

    public SearchResult(List<String> itemLabels) {
        this.itemLabels = itemLabels;
    }

    public List<String> getItemLabels() {
        return itemLabels;
    }

    public void setItemLabels(List<String> itemLabels) {
        this.itemLabels = itemLabels;
    }
}
