package pleasework.kvira72.cars.user.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pleasework.kvira72.cars.entity.Car;
import pleasework.kvira72.cars.model.CarDTO;

import java.util.Optional;
import java.util.Set;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);

    @Query("SELECT u.cars FROM AppUser u WHERE u.username = :username")
    Set<Car> findCarsByUsername(@Param("username") String username);

}
