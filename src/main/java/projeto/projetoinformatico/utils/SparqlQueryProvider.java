package projeto.projetoinformatico.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SparqlQueryProvider {

    @Value("${sparql.prefixes}")
    private String PREFIXES; // Inject SPARQL prefixes

    public String constructSparqlQuery(String sparqlQuery) {
        // Combine common prefixes with the provided SPARQL query
        return PREFIXES + sparqlQuery;
    }

    public String constructSparqlQueryTime(Double lat1, Double lon1, Double lat2, Double lon2, Long startTime, Long endTime) {
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


    public String buildItemQuery(String entityId) {
        return PREFIXES +
                "SELECT DISTINCT ?property ?value WHERE { wd:" + entityId + " ?property ?value. " +
                "FILTER(LANG(?value) = 'en')}";
    }


    public String buildPropertyQuery(String propertyId) {
        // Construct and return SPARQL query to retrieve information about a Wikidata property
        // based on the provided property ID
        return PREFIXES +
                "SELECT DISTINCT ?property ?label WHERE { wd:" + propertyId + " rdfs:label ?label. }";
    }

    public String buildGeoQuery(String itemId) {
        return  PREFIXES +
                "SELECT ?item ?coordinateLocation ?coordinateLocationLabel ?coordinateLocationAltLabel ?coordinateLocationDescription ?coordinateLocationPrecision ?coordinateLocationGlobe\n" +
                "WHERE {\n" +
                "    wd:" + itemId + " p:P625 ?statement.\n" +
                "    ?statement ps:P625 ?coordinateLocation.\n" +
                "    OPTIONAL { ?coordinateLocation rdfs:label ?coordinateLocationLabel. }\n" +
                "    OPTIONAL { ?coordinateLocation skos:altLabel ?coordinateLocationAltLabel. }\n" +
                "    OPTIONAL { ?coordinateLocation schema:description ?coordinateLocationDescription. }\n" +
                "    OPTIONAL { ?coordinateLocation p:P625/psv:P625/wikibase:quantityAmount ?coordinateLocationPrecision. }\n" +
                "    OPTIONAL { ?coordinateLocation p:P625/psv:P625/wikibase:quantityUnit ?coordinateLocationGlobe. }\n" +
                "    SERVICE wikibase:label { bd:serviceParam wikibase:language \"en\". }\n" +
                "}";

    }

    public String buildPropertyItemQuery(String itemId, String propertyId) {
        return  PREFIXES +
                "SELECT DISTINCT ?propertyName ?propertyValue\n" +
                "WHERE {\n" +
                "  wd:" + itemId + " p:" + propertyId + " ?statement.\n" +
                "  ?statement ps:" + propertyId + " ?propertyValue.\n" +
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE],en\". }\n" +
                "}";
    }
    public String buildFilterQuery(String query, Double lat1, Double lon1, Double lat2, Double lon2, Long startTime, Long endTime) {
        String filterClause = generateFilterClause(lat1, lon1, lat2, lon2, startTime, endTime);

        // Find the last occurrence of the innermost SELECT statement
        int selectIndex = query.lastIndexOf("SELECT DISTINCT ?item ?coordinates WHERE {");
        if (selectIndex != -1) {
            // Find the closing brace after the innermost SELECT statement
            int endIndex = query.indexOf("}", selectIndex);
            if (endIndex != -1) {
                // Insert the filterClause before the closing brace
                query = query.substring(0, endIndex) + filterClause + query.substring(endIndex);
            }
        }
        return query;
    }



    private String generateFilterClause(Double lat1, Double lon1, Double lat2, Double lon2, Long startTime, Long endTime) {
        StringBuilder sb = new StringBuilder();
        sb.append("      SERVICE wikibase:box {\n");
        sb.append("        ?place wdt:P625 ?pob.\n"); // coordenadas do local de nascimento
        sb.append("        bd:serviceParam wikibase:cornerSouthWest \"Point(").append(lon1).append(" ").append(lat1).append(")\"^^geo:wktLiteral.\n");
        sb.append("        bd:serviceParam wikibase:cornerNorthEast \"Point(").append(lon2).append(" ").append(lat2).append(")\"^^geo:wktLiteral.\n");
        sb.append("      }\n");
        if (startTime != null && endTime != null) {
            sb.append("      OPTIONAL {\n");
            sb.append("        ?item wdt:P585 ?date.\n");
            sb.append("        FILTER(YEAR(?date) >= ").append(startTime).append(" && YEAR(?date) <= ").append(endTime).append(").\n");
            sb.append("      }\n");
        }
        return sb.toString();
    }

}
