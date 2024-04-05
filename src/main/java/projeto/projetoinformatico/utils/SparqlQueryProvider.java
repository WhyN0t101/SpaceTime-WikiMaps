package projeto.projetoinformatico.utils;

import org.springframework.stereotype.Component;

@Component
public class SparqlQueryProvider {
   private static final String PREFIXES = "PREFIX schema: <http://schema.org/>\n" +
           "PREFIX wikibase: <http://wikiba.se/ontology#>\n" +
           "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
           "PREFIX bd: <http://www.bigdata.com/rdf#>\n" +
           "PREFIX wdt: <http://www.wikidata.org/prop/direct/>\n" +
           "PREFIX wd: <http://www.wikidata.org/entity/>\n" +
           "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n";

    public String constructSparqlQuery(String sparqlQuery) {
        // Combine common prefixes with the provided SPARQL query
        return PREFIXES + sparqlQuery;
    }

    public String constructSparqlQueryTime(Double lat1, Double lon1, Double lat2, Double lon2, Long startTime, Long endTime) {
        // Construct SPARQL query with time filter and common prefixes
        return PREFIXES +
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
                "  OPTIONAL {\n" +
                "    ?item wdt:P585 ?when.\n" +
                "    FILTER(YEAR(?when) >= " + startTime + " && YEAR(?when) <= " + endTime + ").\n" +
                "  }\n" +
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE]\" . }\n" +
                "}\n" +
                "ORDER BY ASC(?when)\n" +
                "LIMIT 1000";
    }

    public String constructSparqlQueryTimeAndCountry(Long year, String country) {
        // Construct SPARQL query with time and country filters and common prefixes
        return PREFIXES +
                "SELECT DISTINCT ?item ?itemLabel ?when ?where ?url\n" +
                "WHERE {\n" +
                "  ?url schema:about ?item .\n" +
                "  ?url schema:inLanguage \"pt\" .\n" +
                "  FILTER (STRSTARTS(str(?url), \"https://pt.wikipedia.org/\")).\n" +
                "  OPTIONAL {\n" +
                "    ?item wdt:P585 ?when .\n" +
                "    FILTER(YEAR(?when) = " + year + ").\n" +
                "  }\n" +
                "  ?item wdt:P17 ?countryItem.\n" + // Filter by country
                "  ?countryItem wdt:P31 wd:Q6256.\n" + // Ensure it's a country
                "  ?countryItem rdfs:label \"" + country + "\"@en.\n" + // Match country label
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE]\" . }\n" +
                "}\n" +
                "ORDER BY ASC(?when)\n" +
                "LIMIT 1000";
    }

   public String constructSparqlQuery(Double lat1, Double lon1, Double lat2, Double lon2) {
       // Determine minimum and maximum latitude and longitude values
       double minLat = Math.min(lat1, lat2);
       double maxLat = Math.max(lat1, lat2);
       double minLon = Math.min(lon1, lon2);
       double maxLon = Math.max(lon1, lon2);

       // Construct SPARQL query with the box filter
       return PREFIXES +
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
               "    bd:serviceParam wikibase:cornerSouthWest \"Point(" + minLon + " " + minLat + ")\"^^geo:wktLiteral .\n" +
               "    bd:serviceParam wikibase:cornerNorthEast \"Point(" + maxLon + " " + maxLat + ")\"^^geo:wktLiteral .\n" +
               "  }\n" +
               "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE]\" . }\n" +
               "}\n" +
               "ORDER BY ASC(?when)\n" +
               "LIMIT 1000";
   }



    public String buildItemQuery(String itemId) {
        // Construct and return SPARQL query to retrieve information about a Wikidata item
        // based on the provided item ID
        return PREFIXES +
                "SELECT DISTINCT ?item ?label WHERE { wd:" + itemId + " rdfs:label ?label. }";
    }

    public String buildPropertyQuery(String propertyId) {
        // Construct and return SPARQL query to retrieve information about a Wikidata property
        // based on the provided property ID
        return PREFIXES +
                "SELECT DISTINCT ?property ?label WHERE { wd:" + propertyId + " rdfs:label ?label. }";
    }

}
