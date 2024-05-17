package projeto.projetoinformatico.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import projeto.projetoinformatico.exceptions.Exception.SparqlQueryNotFoundException;
import projeto.projetoinformatico.model.SearchResult;
import projeto.projetoinformatico.utils.SparqlQueryProvider;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResourceService {

    private final SearchService searchService;
    private final SparqlQueryProvider sparqlQueryProvider;

    @Autowired
    public ResourceService(SearchService searchService, SparqlQueryProvider sparqlQueryProvider) {
        this.searchService = searchService;
        this.sparqlQueryProvider = sparqlQueryProvider;
    }

    @Cacheable(value = "searchCache", key = "{ #itemId, #propertyId }")
    public SearchResult getPropertyValues(String itemId, String propertyId) {
        String sparqlQuery = sparqlQueryProvider.buildPropertyItemQuery(itemId, propertyId);
        return executeSparqlQuery(sparqlQuery);
    }

    @Cacheable(value = "searchCache", key = "{ #itemId }")
    public SearchResult getGeolocationData(String itemId) {
        String sparqlQuery = sparqlQueryProvider.buildGeoQuery(itemId);
        return executeSparqlQuery(sparqlQuery);
    }

    @Cacheable(value = "searchCache", key = "{ #itemId }")
    public SearchResult getItem(String itemId) {
        String sparqlQuery = sparqlQueryProvider.buildItemQuery(itemId);
        return executeSparqlQuery(sparqlQuery);
    }

    @Cacheable(value = "searchCache", key = "{ #propertyId }")
    public SearchResult getProperty(String propertyId) {
        String sparqlQuery = sparqlQueryProvider.buildPropertyQuery(propertyId);
        return executeSparqlQuery(sparqlQuery);
    }

    private SearchResult executeSparqlQuery(String sparqlQuery) {
        SearchResult result = searchService.executeSparqlQuery(sparqlQuery);
        if (result == null) {
            throw new SparqlQueryNotFoundException("SPARQL query returned no results");
        }
        return result;
    }

    public List<SearchResult> getImages(List<String> itemIds) {
        List<SearchResult> results = new ArrayList<>();
        for (String itemId : itemIds) {
            String sparqlQuery = sparqlQueryProvider.buildImageQuery(itemId);
            SearchResult result = executeSparqlQuery(sparqlQuery);
            results.add(result);
        }
        return results;
    }
}
