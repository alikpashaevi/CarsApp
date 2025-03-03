package pleasework.kvira72.cars.user;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pleasework.kvira72.cars.entity.Car;
import pleasework.kvira72.cars.error.NotFoundException;
import pleasework.kvira72.cars.model.CarDTO;
import pleasework.kvira72.cars.model.EngineDTO;
import pleasework.kvira72.cars.persistence.CarsService;
import pleasework.kvira72.cars.user.model.AppUserDTO;
import pleasework.kvira72.cars.user.model.UserRequest;
import pleasework.kvira72.cars.user.persistence.AppUser;
import pleasework.kvira72.cars.user.persistence.AppUserRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
//    private final CarsService carsService;

    public void saveUser(AppUser user) {
        repository.save(user);
    }

    private AppUserDTO convertToAppUserDTO(AppUser user) {
        return new AppUserDTO(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getBalanceInCents(),
                user.getRoles(),
                user.getCars().stream().map(this::convertToCarDTO).collect(Collectors.toSet())
        );
    }

    public void createUser(UserRequest request) {
        AppUser user = new AppUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(request.getRoleIds().stream()
                .map(roleService::getRole)
                .collect(Collectors.toSet()));

        repository.save(user);
    }
//
//    public Set<AppUser> getUsers() {
//        return new HashSet<>(repository.findAll());
//    }

    public Set<AppUserDTO> getUsers() {
        Set<AppUser> users = new HashSet<>(repository.findAll());
        return users.stream().map(this::convertToAppUserDTO).collect(Collectors.toSet());
    }

    public AppUser getUser(String username) {
        return repository.findByUsername(username).orElseThrow(() -> new NotFoundException("User with username " + username + " not found"));
    }

    public AppUserDTO getUserDTO(String username) {
        AppUser user = repository.findByUsername(username).orElseThrow(() -> new NotFoundException("User with username " + username + " not found"));
        return convertToAppUserDTO(user);
    }

    public AppUser getUserById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    }
//
//    public Set<CarDTO> getUserCars(Long username) {
//        Set<Car> cars = repository.findCarsByUsername(username);
//        return cars.stream().map(this::convertToCarDTO).collect(Collectors.toSet());
//    }

    private CarDTO convertToCarDTO(Car car) {
        String ownerUsername = car.getOwners().stream().findFirst().map(AppUser::getUsername).orElse(null);
        return new CarDTO(car.getId(), car.getModel(), car.getYear(), car.isDriveable(),
                car.isForSale(),
                ownerUsername,
                car.getPriceInCents(),
                new EngineDTO(
                        car.getEngine().getId(),
                        car.getEngine().getHorsePower(),
                        car.getEngine().getCapacity()));
    }
}
