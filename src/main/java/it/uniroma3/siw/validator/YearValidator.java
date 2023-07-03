package it.uniroma3.siw.validator;

import java.time.Year;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class YearValidator implements ConstraintValidator<ValidYear, Year> {

    @Override
    public void initialize(ValidYear constraintAnnotation) {
    }

    @Override
    public boolean isValid(Year year, ConstraintValidatorContext context) {
        if (year == null) {
            return false;
        }

        Year currentYear = Year.now();
        int currentYearValue = currentYear.getValue();
        int yearValue = year.getValue();

        return yearValue >= 1895 && yearValue <= currentYearValue;
    }
}
