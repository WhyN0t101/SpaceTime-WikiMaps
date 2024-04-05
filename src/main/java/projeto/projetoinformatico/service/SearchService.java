package projeto.projetoinformatico.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.RDFNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import projeto.projetoinformatico.model.SearchResult;
import projeto.projetoinformatico.utils.SparqlQueryProvider;
import projeto.projetoinformatico.utils.SparqlQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@EnableCaching
@Service
public class SearchService {

    @Value("${sparql.endpoint}")
    private String sparqlEndpoint; // Inject SPARQL endpoint URL
    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    private final SparqlQueryProvider sparqlQueryProvider;

    @Autowired
    public SearchService(SparqlQueryProvider sparqlQueryProvider) {
        this.sparqlQueryProvider = sparqlQueryProvider;
    }

    @Cacheable(value = "searchCache", key = "{ #lat1, #lat2, #lon1, #lon2}")
    public SearchResult performSearch(Double lat1, Double lon1, Double lat2, Double lon2) {
        try {
            validateCoordinates(lat1, lon1, lat2, lon2);
            String sparqlQuery = sparqlQueryProvider.constructSparqlQuery(lat1, lon1, lat2, lon2);
            return executeSparqlQuery(sparqlQuery);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid coordinates provided: " + e.getMessage());
            return new SearchResult(Collections.emptyList());
        }
    }

    @Cacheable(value = "searchCache", key = "{ #lat1, #lat2, #lon1, #lon2, #startTime, #endTime }")
    public SearchResult performSearchTime(Double lat1, Double lon1, Double lat2, Double lon2, Long startTime, Long endTime) {
        try {
            validateCoordinates(lat1, lon1, lat2, lon2);
            String sparqlQuery = sparqlQueryProvider.constructSparqlQueryTime(lat1, lon1, lat2, lon2, startTime, endTime);
            return executeSparqlQuery(sparqlQuery);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid coordinates provided: " + e.getMessage());
            return new SearchResult(Collections.emptyList());
        }
    }

    @Cacheable(value = "searchCache", key = "{ #lat1, #lat2, #lon1, #lon2, #startTime, #endTime }")
    public SearchResult performSearchYear(String country, Long year) {
        try {
            String sparqlQuery = sparqlQueryProvider.constructSparqlQueryTimeAndCountry(year, country);
            return executeSparqlQuery(sparqlQuery);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid coordinates provided: " + e.getMessage());
            return new SearchResult(Collections.emptyList());
        }
    }

    @Cacheable(value = "searchCache", key = "{ #sparqlQuery.hashCode() }")
    public SearchResult perfomSparqlQuery(String sparqlQuery) {
        try {
            return executeSparqlQuery(sparqlQuery);
        } catch (Exception e) {
            logger.error("Error executing SPARQL query: " + sparqlQuery, e);
            throw new SparqlQueryException("Error executing SPARQL query", e);
        }
    }

    private SearchResult executeSparqlQuery(String sparqlQuery) {
        try {
            // Execute the SPARQL query asynchronously
            CompletableFuture<List<Map<String, String>>> futureResults = CompletableFuture.supplyAsync(() -> {
                try (QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlEndpoint, sparqlQuery)) {
                    ResultSet resultSet = qexec.execSelect();
                    return processQueryResults(resultSet);
                }
            });

            // Wait for the asynchronous execution to complete and get the results
            List<Map<String, String>> results = futureResults.get();
            return new SearchResult(results);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error executing SPARQL query: " + sparqlQuery, e);
            throw new SparqlQueryException("Error executing SPARQL query", e);
        }
    }
    public SearchResult executeSparqlQueryFromJsonString(String jsonString) {
        try {
            // Initialize ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();

            // Deserialize JSON string into a Map<String, String>
            Map<String, String> jsonMap = objectMapper.readValue(jsonString, new TypeReference<Map<String, String>>(){});

            // Extract the SPARQL query from the JSON
            String sparqlQuery = jsonMap.get("query");

            // Combine the extracted SPARQL query with common prefixes
            sparqlQuery = sparqlQueryProvider.constructSparqlQuery(sparqlQuery);

            // Execute the SPARQL query and return the result
            return executeSparqlQuery(sparqlQuery);
        } catch (IOException e) {
            logger.error("Error parsing JSON: " + e.getMessage());
            throw new SparqlQueryException("Error parsing JSON", e);
        }
    }


    private List<Map<String, String>> processQueryResults(ResultSet results) {
        List<Map<String, String>> resultList = new ArrayList<>();
        while (results.hasNext()) {
            QuerySolution solution = results.nextSolution();
            Map<String, String> resultMap = new HashMap<>();

            Iterator<String> varNames = solution.varNames();
            while (varNames.hasNext()) {
                String varName = varNames.next();
                RDFNode rdfNode = solution.get(varName);
                if (rdfNode != null) {
                    resultMap.put(varName, rdfNode.toString());
                }
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
