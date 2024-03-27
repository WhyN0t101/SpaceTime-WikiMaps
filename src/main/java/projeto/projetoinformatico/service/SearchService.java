package projeto.projetoinformatico.service;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.RDFNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import projeto.projetoinformatico.model.SearchResult;
import projeto.projetoinformatico.utils.SparqlQueryProvider;
import java.util.*;

@EnableCaching
@Service
public class SearchService {

    @Value("${sparql.endpoint}")
    private String sparqlEndpoint; // Inject SPARQL endpoint URL
    private final SparqlQueryProvider sparqlQuery;

    @Autowired
    public SearchService(SparqlQueryProvider sparqlQuery) {
        this.sparqlQuery = sparqlQuery;
    }
    @Cacheable(value = "searchCache", key = "{ #lat1, #lat2, #lon1, #lon2}")
    public SearchResult performSearch(Double lat1, Double lon1, Double lat2, Double lon2) {
        try {
            // Validate the coordinates
            validateCoordinates(lat1, lon1, lat2, lon2);
            // Proceed with the search
            String sparqlQuery = this.sparqlQuery.constructSparqlQuery(lat1, lon1, lat2, lon2);
            List<Map<String, String>> results = executeSparqlQuery(sparqlQuery);
            return new SearchResult(results);
        } catch (IllegalArgumentException e) {
            // Log the error
            System.err.println("Invalid coordinates provided: " + e.getMessage());
            // Return a response indicating the error
            return new SearchResult(Collections.emptyList());
        }
    }

    @Cacheable(value = "searchCache", key = "{ #lat1, #lat2, #lon1, #lon2, #startTime, #endTime }")
    public SearchResult performSearchTime(Double lat1, Double lon1, Double lat2, Double lon2, Long startTime, Long endTime) {
        try {
            // Validate the coordinates
            validateCoordinates(lat1, lon1, lat2, lon2);

            // Proceed with the search
            String sparqlQuery = this.sparqlQuery.constructSparqlQueryTime(lat1, lon1, lat2, lon2, startTime, endTime);
            List<Map<String, String>> results = executeSparqlQuery(sparqlQuery);
            return new SearchResult(results);
        } catch (IllegalArgumentException e) {
            // Log the error
            System.err.println("Invalid coordinates provided: " + e.getMessage());
            // Return a response indicating the error
            return new SearchResult(Collections.emptyList());
        }
    }
    @Cacheable(value = "searchCache", key = "{ #lat1, #lat2, #lon1, #lon2, #startTime, #endTime }")
    public SearchResult performSearchYear(String country, Long year) {
        try {
            // Proceed with the search
            String sparqlQuery = this.sparqlQuery.constructSparqlQueryTimeAndCountry(year,country);
            List<Map<String, String>> results = executeSparqlQuery(sparqlQuery);
            return new SearchResult(results);
        } catch (IllegalArgumentException e) {
            // Log the error
            System.err.println("Invalid coordinates provided: " + e.getMessage());
            // Return a response indicating the error
            return new SearchResult(Collections.emptyList());
        }
    }

    private List<Map<String, String>> executeSparqlQuery(String sparqlQuery) {
        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlEndpoint, sparqlQuery)) {
            ResultSet results = qexec.execSelect();
            return processQueryResults(results);
        }
    }

    private List<Map<String, String>> processQueryResults(ResultSet results) {
        List<Map<String, String>> resultList = new ArrayList<>();
        while (results.hasNext()) {
            QuerySolution solution = results.nextSolution();
            Map<String, String> resultMap = new HashMap<>();
            RDFNode itemLabelNode = solution.get("itemLabel");
            if (itemLabelNode != null) {
                resultMap.put("itemLabel", itemLabelNode.toString());
            }
            RDFNode whereNode = solution.get("where");
            if (whereNode != null) {
                resultMap.put("where", whereNode.toString());
            }
            RDFNode urlNode = solution.get("url");
            if (urlNode != null) {
                resultMap.put("url", urlNode.toString());
            }
            resultList.add(resultMap);
        }
        return resultList;
    }

    private void validateCoordinates(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            throw new IllegalArgumentException("Latitude and longitude values cannot be null");
        }
        if (lat1 < -90 || lat1 > 90 || lon1 < -180 || lon1 > 180 ||
                lat2 < -90 || lat2 > 90 || lon2 < -180 || lon2 > 180) {
            throw new IllegalArgumentException("Invalid latitude or longitude values");
        }
    }



}


