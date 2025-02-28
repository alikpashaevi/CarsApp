package pleasework.kvira72.cars.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import pleasework.kvira72.cars.model.EngineDTO;

import java.util.List;


public interface EngineRepository extends JpaRepository<Engine, Long> {
    @Query("SELECT NEW pleasework.kvira72.cars.model.EngineDTO(e.id, e.horsePower, e.horsePower)"
            + " FROM Engine e WHERE e.capacity = :capacity")
    Page<EngineDTO> findEngines(double capacity, Pageable pageable);
}
