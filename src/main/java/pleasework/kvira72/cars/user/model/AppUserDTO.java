package pleasework.kvira72.cars.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import pleasework.kvira72.cars.model.CarDTO;
import pleasework.kvira72.cars.user.persistence.Role;

import java.util.Set;

@Data
@AllArgsConstructor
public class AppUserDTO {
    private long id;
    private String username;
    private String password;
    private long balanceInCents;
    private Set<Role> role;
    private CarDTO car;
}
