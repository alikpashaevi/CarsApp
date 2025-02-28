package pleasework.kvira72.cars.model;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EngineRequest {
    @Positive
    private int horsePower;
    @Positive
    private double capacity;
}