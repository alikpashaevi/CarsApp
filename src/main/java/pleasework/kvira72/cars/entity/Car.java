package pleasework.kvira72.cars.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pleasework.kvira72.cars.user.persistence.AppUser;

@Entity
@Table(name = "car")
@SequenceGenerator(name = "car_seq_gen", sequenceName = "car_seq", allocationSize = 1)
@Getter
@Setter
public class Car {

    @Id
    @GeneratedValue(generator = "car_seq_gen", strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "model")
    private String model;

    @Column(name = "year")
    private int year;

    @Column(name = "is_driveable")
    private boolean driveable;

    @Column(name = "price_in_cents")
    private long priceInCents;

    @ManyToOne
    @JoinColumn(name = "engine_id")
    private Engine engine;

}
