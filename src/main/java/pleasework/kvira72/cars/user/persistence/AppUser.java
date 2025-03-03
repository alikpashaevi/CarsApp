package pleasework.kvira72.cars.user.persistence;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pleasework.kvira72.cars.entity.Car;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "app_user", schema = "cars")
@SequenceGenerator(name = "app_user_seq_gen", sequenceName = "user_seq", allocationSize = 1)
@Getter
@Setter
public class AppUser {

    @Id
    @GeneratedValue(generator = "app_user_seq_gen", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column
    private String username;

    @Column
    private String password;

    @Column
    private Long balanceInCents;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_car",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "car_id")
    )
    private Set<Car> cars = new HashSet<>();

    @Override
    public String toString() {
        return "AppUser{id=" + id + ", username='" + username + "'}";
    }

    public void addCar(Car car) {
        this.cars.add(car);
        car.getOwners().add(this); // Update the other side of the relationship
    }

    public void removeCar(Car car) {
        this.cars.remove(car);
        car.getOwners().remove(this); // Update the other side of the relationship
    }

}
