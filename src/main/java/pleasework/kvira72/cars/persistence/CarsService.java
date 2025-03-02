package pleasework.kvira72.cars.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pleasework.kvira72.cars.error.NotFoundException;
import pleasework.kvira72.cars.model.CarDTO;
import pleasework.kvira72.cars.model.CarRequest;
import pleasework.kvira72.cars.model.EngineDTO;
import pleasework.kvira72.cars.entity.Car;
import pleasework.kvira72.cars.entity.CarRepository;
import pleasework.kvira72.cars.user.UserService;
import pleasework.kvira72.cars.user.persistence.AppUser;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarsService {

    private final CarRepository carRepository;
    private final EngineService engineService;
    private final UserService userService;

    public CarDTO mapCar(Car car) {
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

    private NotFoundException buildNotFoundException(long id) {
        return new NotFoundException("Car with id " + id + " not found");
    }

    public Page<CarDTO> getCars(int page, int pageSize) {
        return carRepository.findCars(PageRequest.of(page, pageSize));
    }

    public CarDTO getCar(long id) {
        Car car = carRepository.findById(id).orElseThrow(() -> buildNotFoundException(id));
        return mapCar(car);
    }

    @Transactional
    public void listCarForSale(Long carId, Long ownerId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found"));
        AppUser owner = userService.getUserById(ownerId);

        if (!car.getOwners().contains(owner)) {
            throw new RuntimeException("You are not the owner of this car");
        }

        car.setForSale(true);
        carRepository.save(car);
    }

    @Transactional
    public void purchaseCar(Long carId, Long buyerId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found"));
        AppUser buyer = userService.getUserById(buyerId);

        if (!car.isForSale()) {
            throw new RuntimeException("Car is not for sale");
        }

        if (Objects.equals(buyerId, car.getOwners().stream().findFirst().map(AppUser::getId).orElse(null))) {
            throw new RuntimeException("You are the owner of this car");
        }

        if (buyer.getBalanceInCents() < car.getPriceInCents()) {
            throw new RuntimeException("Insufficient balance");
        }

        Set<AppUser> owners = car.getOwners();
        AppUser seller = owners.iterator().next();

        buyer.setBalanceInCents(buyer.getBalanceInCents() - car.getPriceInCents());

        seller.setBalanceInCents(seller.getBalanceInCents() + car.getPriceInCents());

        owners.remove(seller);
        owners.add(buyer);
        car.setOwners(owners);

        car.setForSale(false);

        userService.saveUser(buyer);
        userService.saveUser(seller);
        carRepository.save(car);
    }

    public Page<CarDTO> getCarsForSale(int page, int pageSize) {
        return carRepository.findCarsForSale(PageRequest.of(page, pageSize));
    }

//    public void removeCarFromSale(Long carId) {
//        Car car = carRepository.findById(carId)
//                .orElseThrow(() -> new RuntimeException("Car not found"));
//        car.setForSale(false);
//        carRepository.save(car);
//    }

    public void addCar(CarRequest request) {
        Car newCar = new Car();
        newCar.setModel(request.getModel());
        newCar.setYear(request.getYear());
        newCar.setDriveable(request.isDriveable());
        newCar.setEngine(engineService.findEngine(request.getEngineId()));
        newCar.setPriceInCents(request.getPriceInCents());

        Set<AppUser> owners = request.getOwner().stream()
                .map(userService::getUserById)
                .collect(Collectors.toSet());

        newCar.setOwners(owners);

        for (AppUser owner : owners) {
            owner.getCars().add(newCar);
        }

        carRepository.save(newCar);
    }

    public void updateCar(long id, CarRequest request) {
        Car car = carRepository.findById(id).orElseThrow(() -> buildNotFoundException(id));
        car.setModel(request.getModel());
        car.setYear(request.getYear());
        car.setPriceInCents(request.getPriceInCents());
        car.setDriveable(request.isDriveable());
        Set<AppUser> owners = request.getOwner().stream()
                .map(userService::getUserById)
                .collect(Collectors.toSet());

        car.setOwners(owners);

        for (AppUser owner : owners) {
            owner.getCars().add(car);
        }
        if (car.getEngine().getId() != request.getEngineId()) {
            car.setEngine(engineService.findEngine(request.getEngineId()));
        }
        carRepository.save(car);
    }

    public void deleteCar(long id) {
        carRepository.deleteById(id);
    }

}
