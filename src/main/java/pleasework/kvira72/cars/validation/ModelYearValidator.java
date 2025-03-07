package pleasework.kvira72.cars.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;

public class ModelYearValidator implements ConstraintValidator<ValidModelYear, Integer> {
    private static final int MIN_YEAR = 1940;

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        int currentYear = Year.now().getValue();
        return value >= MIN_YEAR && value <= currentYear;
    }
}
