package projeto.projetoinformatico.service;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.RDFNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import projeto.projetoinformatico.model.SearchResult;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {


    @Cacheable(value = "searchCache", key = "{ #lat1, #lat2, #lon1, #lon2}")

    public SearchResult performSearch(Double lat1, Double lon1, Double lat2, Double lon2)  {
        String sparqlQuery = constructSparqlQuery(lat1, lon1,lat2,lon2);
        List<Map<String, String>> results = executeSparqlQuery(sparqlQuery);
        return new projeto.projetoinformatico.model.SearchResult(results); // Fully qualify the SearchResult class
    }
    @Cacheable(value = "searchCache", key = "{ #lat1, #lat2, #lon1, #lon2, #startTime, #endTime }")

    public SearchResult performSearchTime(Double lat1, Double lon1, Double lat2, Double lon2, Long startTime, Long endTime)  {
        String sparqlQuery = constructSparqlQueryTime(lat1, lon1,lat2,lon2 ,startTime, endTime);
        List<Map<String, String>> results = executeSparqlQuery(sparqlQuery);
        return new projeto.projetoinformatico.model.SearchResult(results);
    }

    private List<Map<String, String>> executeSparqlQuery(String sparqlQuery) {
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
            List<Map<String, String>> resultList = new ArrayList<>();
            while (results.hasNext()) {
                // Extract variables from each result row and add them to the resultList
                QuerySolution solution = results.nextSolution();
                Map<String, String> resultMap = new HashMap<>();

                // Extract itemLabel
                RDFNode itemLabelNode = solution.get("itemLabel");
                if (itemLabelNode != null) {
                    resultMap.put("itemLabel", itemLabelNode.toString());
                }

                // Extract where
                RDFNode whereNode = solution.get("where");
                if (whereNode != null) {
                    resultMap.put("where", whereNode.toString());
                }

                // Extract url
                RDFNode urlNode = solution.get("url");
                if (urlNode != null) {
                    resultMap.put("url", urlNode.toString());
                }
                resultList.add(resultMap);
            }
            return resultList;
        } finally {
            // Release resources
            qexec.close();
        }
    }


    private String constructSparqlQuery(Double lat1, Double lon1, Double lat2, Double lon2) {
        String sparqlQuery =
                        "PREFIX schema: <http://schema.org/>\n" +
                        "PREFIX wikibase: <http://wikiba.se/ontology#>\n" +
                        "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                        "PREFIX bd: <http://www.bigdata.com/rdf#>\n" +
                        "PREFIX wdt: <http://www.wikidata.org/prop/direct/>\n" +
                        "\n" +
                        "SELECT DISTINCT ?item ?itemLabel ?when ?where ?url\n" +
                        "WHERE {\n" +
                        "  ?url schema:about ?item .\n" +
                        "  ?url schema:inLanguage \"pt\" .\n" +
                        "  FILTER (STRSTARTS(str(?url), \"https://pt.wikipedia.org/\")) .\n" +
                        "  SERVICE wikibase:box {\n" +
                        "    ?item wdt:P625 ?where .\n" +
                        "    bd:serviceParam wikibase:cornerSouthWest \"Point(-9.52 36.95)\"^^geo:wktLiteral .\n" +
                        "    bd:serviceParam wikibase:cornerNorthEast \"Point(-6.18 42.16)\"^^geo:wktLiteral .\n" +
                        "  }\n" +
                        "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE]\" . }\n" +
                        "}\n" +
                        "ORDER BY ASC(?when)\n" +
                        "LIMIT 1000";
        // Return the SPARQL query
        return sparqlQuery;
    }
    private String constructSparqlQueryTime(Double lat1, Double lon1, Double lat2, Double lon2, Long startTime, Long endTime) {
        // Construct the SPARQL query with variables
        String sparqlQuery = "PREFIX schema: <http://schema.org/>\n" +
                "PREFIX wikibase: <http://wikiba.se/ontology#>\n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX bd: <http://www.bigdata.com/rdf#>\n" +
                "PREFIX wdt: <http://www.wikidata.org/prop/direct/>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "\n" +
                "SELECT DISTINCT ?item ?itemLabel ?when ?where ?url\n" +
                "WHERE {\n" +
                "  ?url schema:about ?item .\n" +
                "  ?url schema:inLanguage \"pt\" .\n" +
                "  FILTER (STRSTARTS(str(?url), \"https://pt.wikipedia.org/\")).\n" +
                "  SERVICE wikibase:box {\n" +
                "    ?item wdt:P625 ?where .\n" +
                "    bd:serviceParam wikibase:cornerSouthWest \"Point(" + lon1 + " " + lat1 + ")\"^^geo:wktLiteral.\n" +
                "    bd:serviceParam wikibase:cornerNorthEast \"Point(" + lon2 + " " + lat2 + ")\"^^geo:wktLiteral.\n" +
                "  }\n" +
                "  OPTIONAL { \n" +
                "    ?item wdt:P585 ?when . \n" +
                "    BIND (YEAR(?when) AS ?year) .\n" +
                "    FILTER(?year >= " + startTime + " && ?year <= " + endTime + ").\n" +
                "  }\n" +
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE]\" . }\n" +
                "}\n" +
                "ORDER BY ASC(?when)\n" +
                "LIMIT 1000\n" +
                "";

        // Return the SPARQL query
        return sparqlQuery;
    }



}


