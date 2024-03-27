package projeto.projetoinformatico.utils;

import org.springframework.stereotype.Component;

@Component
public class SparqlQueryProvider {
   /* public String constructSparqlQuery(Double lat1, Double lon1, Double lat2, Double lon2) {
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
                "    bd:serviceParam wikibase:cornerSouthWest \"Point(" + lon1 + " " + lat1 + ")\"^^geo:wktLiteral .\n" +
                "    bd:serviceParam wikibase:cornerNorthEast \"Point("+ lon2 + " " + lat2 +")\"^^geo:wktLiteral .\n" +
                "  }\n" +
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE]\" . }\n" +
                "}\n" +
                "ORDER BY ASC(?when)\n" +
                "LIMIT 1000";
    }*/
   public String constructSparqlQuery(Double lat1, Double lon1, Double lat2, Double lon2) {
       // Determine minimum and maximum latitude and longitude values
       double minLat = Math.min(lat1, lat2);
       double maxLat = Math.max(lat1, lat2);
       double minLon = Math.min(lon1, lon2);
       double maxLon = Math.max(lon1, lon2);

       // Construct SPARQL query with the box filter
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
               "    bd:serviceParam wikibase:cornerSouthWest \"Point(" + minLon + " " + minLat + ")\"^^geo:wktLiteral .\n" +
               "    bd:serviceParam wikibase:cornerNorthEast \"Point(" + maxLon + " " + maxLat + ")\"^^geo:wktLiteral .\n" +
               "  }\n" +
               "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE]\" . }\n" +
               "}\n" +
               "ORDER BY ASC(?when)\n" +
               "LIMIT 1000";
   }


    public String constructSparqlQueryTime(Double lat1, Double lon1, Double lat2, Double lon2, Long startTime, Long endTime) {

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
    public String constructSparqlQueryTimeAndCountry(Long year, String country) {
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
                "  FILTER (STRSTARTS(str(?url), \"https://pt.wikipedia.org/\")).\n" +
                "  OPTIONAL { \n" +
                "    ?item wdt:P585 ?when . \n" +
                "    BIND (YEAR(?when) AS ?itemYear) .\n" +
                "    FILTER(?itemYear = " + year + ").\n" +
                "  }\n" +
                "  ?item wdt:P17 ?countryItem.\n" + // Filter by country
                "  ?countryItem wdt:P31 wd:Q6256.\n" + // Ensure it's a country
                "  ?countryItem rdfs:label \"" + country + "\"@en.\n" + // Match country label
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE]\" . }\n" +
                "}\n" +
                "ORDER BY ASC(?when)\n" +
                "LIMIT 1000\n" +
                "";
    }
}
