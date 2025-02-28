package pleasework.kvira72.cars.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pleasework.kvira72.cars.model.CarDTO;

public interface CarRepository  extends JpaRepository<Car, Long> {

    @Query(
            "SELECT NEW pleasework.kvira72.cars.model.CarDTO" +
                    "(c.id, c.model, c.year, c.driveable, " +
                    "new pleasework.kvira72.cars.model.EngineDTO" +
                    "(e.id, e.horsePower, e.capacity)) " +
                    "FROM Car c JOIN c.engine e"
    )
    Page<CarDTO> findCars(Pageable pageable);

}
