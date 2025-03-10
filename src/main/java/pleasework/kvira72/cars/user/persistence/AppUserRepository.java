package pleasework.kvira72.cars.user.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);

//    @Query("SELECT u.cars FROM AppUser u WHERE u.username = :username")
//    Set<CarDTO> findCarsByUsername(@Param("username") String username);
//
//    @Query("SELECT NEW pleasework.kvira72.cars.user.model.AppUserDTO(u.id, u.username, u.password, u.balanceInCents, u.roles, " +
//            "NEW pleasework.kvira72.cars.model.CarDTO(c.id, c.model, c.year, c.driveable, c.forSale, o.username, c.priceInCents, NEW pleasework.kvira72.cars.model.EngineDTO(e.id, e.horsePower, e.capacity))) " +
//            "FROM AppUser u " +
//            "JOIN u.cars c " +
//            "JOIN c.engine e " +
//            "LEFT JOIN c.owners o " +
//            "WHERE u.username = :username")
//    Optional<AppUserDTO> findUserWithCarsByUsername(@Param("username") String username);
}
