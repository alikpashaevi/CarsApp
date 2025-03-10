package pleasework.kvira72.cars.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pleasework.kvira72.cars.user.persistence.AppUser;

import java.util.HashSet;
import java.util.Set;

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

    @Column(name = "for_sale")
    private boolean forSale;

    @ManyToOne
    @JoinColumn(name = "engine_id")
    private Engine engine;

    @ManyToMany(mappedBy = "cars")
    private Set<AppUser> owners = new HashSet<>();

    @Column(name = "photo_url")
    private String photoUrl;

    public void addOwner(AppUser owner) {
        this.owners.add(owner);
        owner.getCars().add(this);
    }

    public void removeOwner(AppUser owner) {
        this.owners.remove(owner);
        owner.getCars().remove(this);
    }

}
