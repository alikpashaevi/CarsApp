package pleasework.kvira72.cars.user.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "role", schema = "cars")
@Getter
@Setter
public class Role {

    @Id
    private Long id;

    @Column
    private String name;
}