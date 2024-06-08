package projeto.projetoinformatico.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import projeto.projetoinformatico.utils.SparqlQueryProvider;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SparqlQueryProviderTest {

    @Autowired
    private SparqlQueryProvider sparqlQueryProvider;
    @Value("${sparql.prefixes}")
    private String prefixes;



    @Test
    void constructSparqlQuery() {
        // Given
        String sparqlQuery = "SELECT * WHERE {?s ?p ?o}";

        // When
        String result = sparqlQueryProvider.constructSparqlQuery(sparqlQuery);

        // Then
        String expected = "PREFIX bd: <http://www.bigdata.com/rdf#>\nPREFIX cc: <http://creativecommons.org/ns#>\nPREFIX dct: <http://purl.org/dc/terms/>\nPREFIX geo: <http://www.opengis.net/ont/geosparql#>\nPREFIX hint: <http://www.bigdata.com/queryHints#>\nPREFIX ontolex: <http://www.w3.org/ns/lemon/ontolex#>\nPREFIX owl: <http://www.w3.org/2002/07/owl#>\nPREFIX prov: <http://www.w3.org/ns/prov#>\nPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\nPREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\nPREFIX schema: <http://schema.org/>\nPREFIX skos: <http://www.w3.org/2004/02/skos/core#>\nPREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\nPREFIX p: <http://www.wikidata.org/prop/>\nPREFIX pq: <http://www.wikidata.org/prop/qualifier/>\nPREFIX pqn: <http://www.wikidata.org/prop/qualifier/value-normalized/>\nPREFIX pqv: <http://www.wikidata.org/prop/qualifier/value/>\nPREFIX pr: <http://www.wikidata.org/prop/reference/>\nPREFIX prn: <http://www.wikidata.org/prop/reference/value-normalized/>\nPREFIX prv: <http://www.wikidata.org/prop/reference/value/>\nPREFIX psv: <http://www.wikidata.org/prop/statement/value/>\nPREFIX ps: <http://www.wikidata.org/prop/statement/>\nPREFIX psn: <http://www.wikidata.org/prop/statement/value-normalized/>\nPREFIX wd: <http://www.wikidata.org/entity/>\nPREFIX wdata: <http://www.wikidata.org/wiki/Special:EntityData/>\nPREFIX wdno: <http://www.wikidata.org/prop/novalue/>\nPREFIX wdref: <http://www.wikidata.org/reference/>\nPREFIX wds: <http://www.wikidata.org/entity/statement/>\nPREFIX wdt: <http://www.wikidata.org/prop/direct/>\nPREFIX wdtn: <http://www.wikidata.org/prop/direct-normalized/>\nPREFIX wdv: <http://www.wikidata.org/value/>\nPREFIX wikibase: <http://wikiba.se/ontology#>SELECT * WHERE {?s ?p ?o}";
        assertEquals(expected, result);
    }

    @Test
    void generateFilterClause() {
        // Given
        Double lat1 = 40.7128;
        Double lon1 = -74.0060;
        Double lat2 = 34.0522;
        Double lon2 = -118.2437;
        Long startTime = 2000L;
        Long endTime = 2022L;

        // When
        String filterClause = sparqlQueryProvider.generateFilterClause(lat1, lon1, lat2, lon2, startTime, endTime);

        // Then
        String expected = "      SERVICE wikibase:box {\n" +
                "        ?place wdt:P625 ?pob.\n" +
                "        bd:serviceParam wikibase:cornerSouthWest \"Point(-74.006 40.7128)\"^^geo:wktLiteral.\n" +
                "        bd:serviceParam wikibase:cornerNorthEast \"Point(-118.2437 34.0522)\"^^geo:wktLiteral.\n" +
                "      }\n" +
                "      OPTIONAL {\n" +
                "        ?item wdt:P585 ?date.\n" +
                "        FILTER(YEAR(?date) >= 2000 && YEAR(?date) <= 2022).\n" +
                "      }\n";
        assertEquals(expected, filterClause);
    }

    @Test
    void buildFilterQuery() {
        // Test buildFilterQuery method with sample inputs
        SparqlQueryProvider provider = new SparqlQueryProvider();
        String query = "SELECT DISTINCT ?item ?coordinates WHERE { ... }";
        String filterQuery = provider.buildFilterQuery(query, 1.0, 2.0, 3.0, 4.0, 2020L, 2021L);

        // Assert that the filterQuery is not null or empty
        assertNotNull(filterQuery);
        assertFalse(filterQuery.isEmpty());
        // You may add more assertions based on your specific requirements
    }

    @Test
    void isSparqlQueryValid() {
        SparqlQueryProvider provider = new SparqlQueryProvider();
        String validQuery = "SELECT DISTINCT ?item ?itemLabel ?description ?coordinates ?image ?itemSchemaLabel ?url WHERE {\\n  SERVICE wikibase:label { bd:serviceParam wikibase:language \\\"[AUTO_LANGUAGE]\\\". }\\n  {\\n    SELECT DISTINCT ?item ?itemLabel ?coordinates ?itemSchemaLabel ?url WHERE {\\n      ?item p:P31 ?statement0.\\n      ?statement0 (ps:P31/(wdt:P279*)) wd:Q5.\\n      ?item wdt:P19 ?place.\\n      ?place wdt:P625 ?coordinates.\\n      ?item wdt:P1448 ?itemSchemaLabel.\\n      FILTER(LANG(?itemSchemaLabel) = \\\"en\\\").\\n      \\n      ?url schema:about ?item .\\n      ?url schema:inLanguage \\\"en\\\" .\\n      FILTER(STRSTARTS(str(?url), \\\"https://en.wikipedia.org/\\\"))\\n    }\\n    LIMIT 10\\n  }\\n  OPTIONAL { \\n    ?item schema:description ?description.\\n    FILTER(LANG(?description) = \\\"en\\\")\\n  }\\n  OPTIONAL {\\n    ?item wdt:P18 ?image.\\n  }\\n}";
        boolean isValid = provider.isSparqlQueryValid(validQuery);

        assertTrue(isValid);
    }
    @Test
    public void testBuildPropertyQuery() {
        String propertyId = "P123";

        String expectedQuery = prefixes +
                "SELECT DISTINCT ?property ?label WHERE { wd:" + propertyId + " rdfs:label ?label. }";

        assertEquals(expectedQuery, sparqlQueryProvider.buildPropertyQuery(propertyId));
    }

    @Test
    public void testConstructSparqlQuery() {
        String sparqlQuery = "SELECT * WHERE { ?s ?p ?o }";

        String expectedQuery = prefixes +
                sparqlQuery;

        assertEquals(expectedQuery, sparqlQueryProvider.constructSparqlQuery(sparqlQuery));
    }

    @Test
    public void testBuildItemQuery() {
        String entityId = "Q123";

        String expectedQuery = prefixes +
                "SELECT DISTINCT ?property ?value WHERE { wd:" + entityId + " ?property ?value. " +
                "FILTER(LANG(?value) = 'en')}";

        assertEquals(expectedQuery, sparqlQueryProvider.buildItemQuery(entityId));
    }

    @Test
    public void testBuildPropertyItemQuery() {
        String itemId = "Q123";
        String propertyId = "P456";

        String expectedQuery = prefixes +
                "SELECT DISTINCT ?propertyName ?propertyValue\n" +
                "WHERE {\n" +
                "  wd:" + itemId + " p:" + propertyId + " ?statement.\n" +
                "  ?statement ps:" + propertyId + " ?propertyValue.\n" +
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE],en\". }\n" +
                "}";

        assertEquals(expectedQuery, sparqlQueryProvider.buildPropertyItemQuery(itemId, propertyId));
    }
}
