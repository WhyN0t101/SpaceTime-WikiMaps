package projeto.projetoinformatico.controller;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
public class SearchController {

    @GetMapping("/search")
    public String searchWikidata(@RequestParam("query") String query) {
        String sparqlEndpoint = "https://query.wikidata.org/sparql";

        // URL-encode the query string
        query = URLEncoder.encode(query, StandardCharsets.UTF_8);

        // Create a SPARQL query
        String sparqlQuery = String.format("SELECT ?item ?itemLabel WHERE {\n" +
                "  ?item wdt:P31 wd:Q5.\n" +
                "  ?item wdt:P1545 \"%s\".\n" +
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"en\". }\n" +
                "}", query);

        // Execute the query against the SPARQL endpoint
        QueryExecution queryExecution = QueryExecutionFactory.sparqlService(sparqlEndpoint, QueryFactory.create(sparqlQuery));

        // Get the results of the query
        ResultSet resultSet = queryExecution.execSelect();

        // Format the results as a string
        String results = ResultSetFormatter.asText(resultSet);

        // Close the query execution
        queryExecution.close();

        return results;
    }
}