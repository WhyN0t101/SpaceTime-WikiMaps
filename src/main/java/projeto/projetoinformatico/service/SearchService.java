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


    @Cacheable(value = "searchCache", key = "{ #lat1, #lat2, #lon1, #lon2, #startTime, #endTime }")

    public SearchResult performSearch(Double lat1, Double lon1, Double lat2, Double lon2, Long startTime, Long endTime)  {
        String sparqlQuery = constructSparqlQuery(lat1, lon1,lat2,lon2 ,startTime, endTime);
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

                //DEFINE LABEL TO OBTAIN <<<<--------------
                RDFNode itemLabelNode = solution.get("personLabel");
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

    private String constructSparqlQuery(Double lat1, Double lon1, Double lat2, Double lon2, Long startTime, Long endTime) {
        // Construct the SPARQL query with variables
        String sparqlQuery = "PREFIX wd: <http://www.wikidata.org/entity/>\n" +
                "PREFIX wdt: <http://www.wikidata.org/prop/direct/>\n" +
                "PREFIX schema: <http://schema.org/>\n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "\n" +
                "SELECT ?person ?personLabel ?birthdate\n" +
                "WHERE\n" +
                "{\n" +
                "  ?person wdt:P31 wd:Q5;          # Instance of human\n" +
                "          wdt:P21 wd:Q6581097;    # Gender male\n" +
                "          wdt:P19 ?placeOfBirth; # Place of birth\n" +
                "          wdt:P569 ?birthdate.   # Date of birth\n" +
                "  \n" +
                "  FILTER(YEAR(?birthdate) = 2002)\n" +
                "  FILTER(?placeOfBirth IN (wd:Q45, wd:Q29))  # Filter for Portugal and Spain\n" +
                "  \n" +
                "  ?placeOfBirth wdt:P625 ?coord.\n" +
                "  FILTER(?coord >= \"Point(" + lon1 + " " + lat1 + ")\"^^geo:wktLiteral && ?coord <= \"Point(" + lon2 + " " + lat2 + ")\"^^geo:wktLiteral)  # Bounding box covering Portugal and Spain\n" +
                "  \n" +
                "  ?person rdfs:label ?personLabel.\n" +
                "  FILTER(LANG(?personLabel) = \"en\") # Filter for English labels\n" +
                "}\n";

        // Return the SPARQL query
        return sparqlQuery;
    }



}


