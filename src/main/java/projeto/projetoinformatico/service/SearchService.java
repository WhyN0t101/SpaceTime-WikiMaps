package projeto.projetoinformatico.service;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.RDFNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import projeto.projetoinformatico.model.SearchResult;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {


    @Cacheable(value = "searchCache", key = "{ #latitude, #longitude, #startTime, #endTime }")

    public SearchResult performSearch(Double latitude, Double longitude, Long startTime, Long endTime) {
        String sparqlQuery = constructSparqlQuery(latitude, longitude, startTime, endTime);
        List<String> results = executeSparqlQuery(sparqlQuery);
        return new projeto.projetoinformatico.model.SearchResult(results); // Fully qualify the SearchResult class
    }


    private List<String> executeSparqlQuery(String sparqlQuery) {
        // Define the Wikidata SPARQL endpoint URL
        String sparqlEndpoint = "https://query.wikidata.org/sparql";

        // Create a Query instance
        Query query = QueryFactory.create(sparqlQuery);

        // Create a QueryExecution instance using the provided SPARQL endpoint
        QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlEndpoint, query);

        try {
            // Execute the query and obtain the result set
            ResultSet results = qexec.execSelect();

            // Process the result set and extract relevant information
            List<String> resultList = new ArrayList<>();
            while (results.hasNext()) {
                // Extract item label from each result row and add it to the resultList
                QuerySolution solution = results.nextSolution();
                RDFNode itemLabelNode = solution.get("itemLabel");
                if (itemLabelNode != null) {
                    resultList.add(itemLabelNode.toString());
                }
            }

            return resultList;
        } finally {
            // Release resources
            qexec.close();
        }
    }

    private String constructSparqlQuery(Double latitude, Double longitude, Long startTime, Long endTime) {
        return "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX time: <http://www.w3.org/2006/time#>\n" +
                "PREFIX wikibase: <http://wikiba.se/ontology#>\n" +
                "PREFIX wd: <http://www.wikidata.org/entity/>\n" +
                "PREFIX wdt: <http://www.wikidata.org/prop/direct/>\n" + // Add this line for Wikidata properties

                "SELECT DISTINCT ?item ?itemLabel ?when ?where ?url\n" +
                "WHERE {\n" +
                "  ?url schema:about ?item .\n" +
                "  ?url schema:inLanguage \"pt\" .\n" +
                "  FILTER (STRSTARTS(str(?url), \"https://pt.wikipedia.org/\")) .\n" +
                "  SERVICE wikibase:box {\n" +
                "    ?item wdt:P625 ?where .\n" +
                "    bd:serviceParam wikibase:cornerSouthWest \"Point(" + longitude + " " + latitude + ")\"^^geo:wktLiteral.\n" +
                "    bd:serviceParam wikibase:cornerNorthEast \"Point(" + longitude + " " + latitude + ")\"^^geo:wktLiteral.\n" +
                "  }\n" +
                "  OPTIONAL { \n" +
                "    ?item wdt:P585 ?when . \n" +
                "    FILTER(YEAR(?when) >= " + startTime + " && YEAR(?when) <= " + endTime + ") .\n" + // Adjusted to filter by startTime and endTime
                "  }\n" +
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE],pt\" . }\n" +
                "}\n" +
                "ORDER BY ASC(?when)\n" +
                "LIMIT 1000";
    }

}
