package projeto.projetoinformatico.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import projeto.projetoinformatico.utils.Validation;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationTest {

    private Validation validation;

    @BeforeEach
    void setUp() {
        validation = new Validation();
    }

    @Test
    void testIsValidCoordinate_ValidCoordinates() {
        assertTrue(validation.isValidCoordinate(12.345, 67.89, -45.678, -90.0));
    }

    @Test
    void testIsValidCoordinate_Lat1OutOfRange() {
        assertFalse(validation.isValidCoordinate(-91.0, 67.89, -45.678, -90.0));
    }

    @Test
    void testIsValidCoordinate_Lon1OutOfRange() {
        assertFalse(validation.isValidCoordinate(12.345, -181.0, -45.678, -90.0));
    }

    @Test
    void testIsValidCoordinate_Lat2OutOfRange() {
        assertFalse(validation.isValidCoordinate(12.345, 67.89, 91.0, -90.0));
    }

    @Test
    void testIsValidCoordinate_Lon2OutOfRange() {
        assertFalse(validation.isValidCoordinate(12.345, 67.89, -45.678, 181.0));
    }

    @Test
    void testIsValidCoordinate_NullInput() {
        assertFalse(validation.isValidCoordinate(null, 67.89, -45.678, -90.0));
    }

    @Test
    void testIsValidYear_ValidYear() {
        assertTrue(validation.isValidYear(1990L));
    }

    @Test
    void testIsValidYear_NegativeYear() {
        assertFalse(validation.isValidYear(-100L));
    }

    @Test
    void testIsValidYear_FutureYear() {
        assertFalse(validation.isValidYear((long) (Year.now().getValue() + 1)));
    }

    @Test
    void testIsValidYear_NullInput() {
        assertFalse(validation.isValidYear(null));
    }

    @Test
    void testIsValidYearRange_ValidRange() {
        assertTrue(validation.isValidYearRange(1990L, 2020L));
    }

    @Test
    void testIsValidYearRange_EndBeforeStart() {
        assertFalse(validation.isValidYearRange(2020L, 1990L));
    }

    @Test
    void testIsValidYearRange_InvalidStartYear() {
        assertFalse(validation.isValidYearRange(-100L, 2020L));
    }

    @Test
    void testIsValidYearRange_InvalidEndYear() {
        assertFalse(validation.isValidYearRange(1990L, (long) (Year.now().getValue() + 1)));
    }

    @Test
    void testIsValidYearRange_NullStartYear() {
        assertFalse(validation.isValidYearRange(null, 2020L));
    }

    @Test
    void testIsValidYearRange_NullEndYear() {
        assertFalse(validation.isValidYearRange(1990L, null));
    }

    @Test
    void testIsValidYearRange_NullStartAndEndYear() {
        assertFalse(validation.isValidYearRange(null, null));
    }
}
