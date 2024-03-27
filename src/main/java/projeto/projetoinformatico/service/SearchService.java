package projeto.projetoinformatico.service;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.RDFNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import projeto.projetoinformatico.model.SearchResult;

import java.util.*;

@EnableCaching
@Service
public class SearchService {

    @Value("${sparql.endpoint}")
    private String sparqlEndpoint; // Inject SPARQL endpoint URL

    @Cacheable(value = "searchCache", key = "{ #lat1, #lat2, #lon1, #lon2}")
    public SearchResult performSearch(Double lat1, Double lon1, Double lat2, Double lon2) {
        try {
            // Validate the coordinates
            validateCoordinates(lat1, lon1, lat2, lon2);
            // Proceed with the search
            String sparqlQuery = constructSparqlQuery(lat1, lon1, lat2, lon2);
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
            String sparqlQuery = constructSparqlQueryTime(lat1, lon1, lat2, lon2, startTime, endTime);
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


    private String constructSparqlQuery(Double lat1, Double lon1, Double lat2, Double lon2) {
        // Return the SPARQL query
        return "PREFIX schema: <http://schema.org/>\n" +
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
    }
    private String constructSparqlQueryTime(Double lat1, Double lon1, Double lat2, Double lon2, Long startTime, Long endTime) {
        // Construct the SPARQL query with variables

        // Return the SPARQL query
        return "PREFIX schema: <http://schema.org/>\n" +
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


