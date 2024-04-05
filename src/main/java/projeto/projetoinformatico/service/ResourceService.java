package projeto.projetoinformatico.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import projeto.projetoinformatico.model.SearchResult;
import projeto.projetoinformatico.utils.SparqlQueryProvider;

@Service
public class ResourceService {

    private final SearchService searchService;
    private final SparqlQueryProvider sparqlQueryProvider;

    @Autowired
    public ResourceService(SearchService searchService, SparqlQueryProvider sparqlQueryProvider) {
        this.searchService = searchService;
        this.sparqlQueryProvider = sparqlQueryProvider;
    }

    @Cacheable(value = "searchCache", key = "{ #itemId }")
    public SearchResult getItem(String itemId) {
        String sparqlQuery = sparqlQueryProvider.buildItemQuery(itemId);
        return searchService.perfomSparqlQuery(sparqlQuery);
    }

    @Cacheable(value = "searchCache", key = "{ #propertyId }")
    public SearchResult getProperty(String propertyId) {
        String sparqlQuery = sparqlQueryProvider.buildPropertyQuery(propertyId);
        return searchService.perfomSparqlQuery(sparqlQuery);
    }
}
