package pleasework.kvira72.cars.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CarDTO {
    private long id;
    private String model;
    private int year;
    private boolean driveable;
    private String owner;
    private long priceInCents;
    private EngineDTO engine;
}
