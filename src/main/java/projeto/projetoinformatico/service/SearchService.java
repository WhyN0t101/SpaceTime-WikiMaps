package projeto.projetoinformatico.service;

import com.fasterxml.jackson.core.type.TypeReference;
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
import projeto.projetoinformatico.exceptions.Exception.SparqlQueryException;
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
    private String sparqlEndpoint;

    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    private final SparqlQueryProvider sparqlQueryProvider;
    private final ObjectMapper objectMapper;

    @Autowired
    public SearchService(SparqlQueryProvider sparqlQueryProvider, ObjectMapper objectMapper) {
        this.sparqlQueryProvider = sparqlQueryProvider;
        this.objectMapper = objectMapper;
    }

    @Cacheable(value = "searchCache", key = "{ #sparqlQuery.hashCode() }")
    public SearchResult executeSparqlQuery(String sparqlQuery) {
        try {
            String sparqlQueryWithPrefixes = sparqlQueryProvider.constructSparqlQuery(sparqlQuery); // Prepend prefixes
            CompletableFuture<List<Map<String, String>>> futureResults = CompletableFuture.supplyAsync(() -> {
                try (QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlEndpoint, sparqlQueryWithPrefixes)) {
                    ResultSet resultSet = qexec.execSelect();
                    return processQueryResults(resultSet);
                }
            });

            List<Map<String, String>> results = futureResults.get();
            return new SearchResult(results);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error executing SPARQL query: " + sparqlQuery, e);
            throw new SparqlQueryException("Error executing SPARQL query");
        }
    }


    public SearchResult executeSparqlQueryFromJsonString(String jsonString) {
        try {
            Map<String, String> jsonMap = objectMapper.readValue(jsonString, new TypeReference<Map<String, String>>() {
            });
            String sparqlQuery = jsonMap.get("query");
            sparqlQuery = sparqlQueryProvider.constructSparqlQuery(sparqlQuery);
            return executeSparqlQuery(sparqlQuery);
        } catch (IOException e) {
            logger.error("Error parsing JSON: " + e.getMessage());
            throw new SparqlQueryException("Error parsing JSON");
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
}
