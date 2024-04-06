package projeto.projetoinformatico.utils;

import org.springframework.stereotype.Component;

import java.time.Year;

@Component
public class Validation {

    public boolean isValidCoordinate(Double lat1, Double lon1, Double lat2, Double lon2) {
        return lat1 != null && lon1 != null && lat2 != null && lon2 != null &&
                lat1 >= -90 && lat1 <= 90 && lon1 >= -180 && lon1 <= 180 &&
                lat2 >= -90 && lat2 <= 90 && lon2 >= -180 && lon2 <= 180;
    }

    public boolean isValidYear(Long year) {
        final int MIN_YEAR = 0;  // Assuming year 0 or later is valid
        final int MAX_YEAR = Year.now().getValue();  // Get the current year
        return year != null && year >= MIN_YEAR && year <= MAX_YEAR;
    }

    public boolean isValidYearRange(Long startYear, Long endYear) {
        return isValidYear(startYear) && isValidYear(endYear) && startYear <= endYear;
    }
}
