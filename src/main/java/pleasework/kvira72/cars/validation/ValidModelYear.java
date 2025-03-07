package pleasework.kvira72.cars.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ModelYearValidator.class)
public @interface ValidModelYear {
    String message() default "Release year must be between and the current year";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}