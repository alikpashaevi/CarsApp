package pleasework.kvira72.cars.model;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import pleasework.kvira72.cars.validation.ValidModelYear;

import java.util.Set;

@Data
@AllArgsConstructor
public class CarRequest {
    @NotBlank
    @Size(max=20)
    private String model;
    @ValidModelYear
    private int year;
    private boolean driveable;
    @Positive
    private Long engineId;
    @Positive
    private Long priceInCents;
    private Set<Long> owner;
}