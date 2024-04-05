package projeto.projetoinformatico.utils;
public class SparqlQueryException extends RuntimeException {
    public SparqlQueryException(String message) {
        super(message);
    }

    public SparqlQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}