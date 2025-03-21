package pleasework.kvira72.cars.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pleasework.kvira72.cars.components.MapCar;
import pleasework.kvira72.cars.error.NotFoundException;
import pleasework.kvira72.cars.model.CarDTO;
import pleasework.kvira72.cars.model.CarRequest;
import pleasework.kvira72.cars.persistence.Car;
import pleasework.kvira72.cars.persistence.CarRepository;
import pleasework.kvira72.cars.user.UserService;
import pleasework.kvira72.cars.user.persistence.AppUser;

import java.text.ParseException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarsService {

    private final CarRepository carRepository;
    private final EngineService engineService;
    private final UserService userService;

    private NotFoundException buildNotFoundException(long id) {
        return new NotFoundException("Car with id " + id + " not found");
    }

    public String getUsernameFromToken() {
        return SecurityContextHolder.getContext().getAuthentication().getName();

    }

    public Page<CarDTO> getCars(int page, int pageSize) {
        return carRepository.findCars(PageRequest.of(page, pageSize));
    }

    public CarDTO getCar(long id) {
        Car car = carRepository.findById(id).orElseThrow(() -> buildNotFoundException(id));
        return MapCar.mapCar(car);
    }

    public Page<CarDTO> searchCars(String model, int page, int pageSize) {
        return carRepository.searchCarsByModelName(model, PageRequest.of(page, pageSize));
    }

    public void listCarForSale(Long carId, Long priceInCents)  {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> buildNotFoundException(carId));

        if (car.isForSale()) {
            throw new RuntimeException("Car is already for sale");
        }
        String username = getUsernameFromToken();

        AppUser owner = userService.getUser(username);

        if (!car.getOwners().contains(owner)) {
            throw new RuntimeException("You are not the owner of this car");
        }
        car.setPriceInCents(priceInCents);
        car.setForSale(true);
        carRepository.save(car);
    }

    public void purchaseCar(Long carId) throws ParseException, JsonProcessingException {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new NotFoundException("Car not found"));

        String username = getUsernameFromToken();

        AppUser buyer = userService.getUser(username);

        if (!car.isForSale()) {
            throw new RuntimeException("Car is not for sale");
        }

        if (car.getOwners().stream().anyMatch(owner -> Objects.equals(owner.getUsername(), username))) {
            car.setForSale(false);
            throw new RuntimeException("Sale cancelled");
        }

        if (buyer.getBalanceInCents() < car.getPriceInCents()) {
            throw new RuntimeException("Insufficient balance");
        }

        // to look for
        Set<AppUser> owners = car.getOwners();
        AppUser seller = owners.iterator().next();

        buyer.setBalanceInCents(buyer.getBalanceInCents() - car.getPriceInCents());

        seller.setBalanceInCents(seller.getBalanceInCents() + car.getPriceInCents());

        car.removeOwner(seller);
        car.addOwner(buyer);

        car.setForSale(false);

        // saving
        userService.saveUser(buyer);
        userService.saveUser(seller);
        carRepository.save(car);
    }

    public void cancelSale(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new NotFoundException("Car not found"));

        String username = getUsernameFromToken();

        AppUser owner = userService.getUser(username);

        if (!car.isForSale()) {
            throw new RuntimeException("Car is not for sale");
        }

        if (!car.getOwners().contains(owner)) {
            throw new RuntimeException("You are not the owner of this car");
        }

        car.setForSale(false);
        carRepository.save(car);
    }

    public Page<CarDTO> getCarsForSale(int page, int pageSize) {
        return carRepository.findCarsForSale(PageRequest.of(page, pageSize));
    }

    public void addCar(CarRequest request, String photoUrl) {
        Car newCar = new Car();
        newCar.setModel(request.getModel());
        newCar.setYear(request.getYear());
        newCar.setDriveable(request.isDriveable());
        newCar.setEngine(engineService.findEngine(request.getEngineId()));
        newCar.setPriceInCents(request.getPriceInCents());
        setAttributesToCar(request, photoUrl, newCar);

        carRepository.save(newCar);
    }

    public void updateCar(long id, CarRequest request, String photoUrl) {
        Car car = carRepository.findById(id).orElseThrow(() -> buildNotFoundException(id));
        car.setModel(request.getModel());
        car.setYear(request.getYear());
        car.setPriceInCents(request.getPriceInCents());
        car.setDriveable(request.isDriveable());
        setAttributesToCar(request, photoUrl, car);
        if (car.getEngine().getId() != request.getEngineId()) {
            car.setEngine(engineService.findEngine(request.getEngineId()));
        }
        carRepository.save(car);
    }

    private void setAttributesToCar(CarRequest request, String photoUrl, Car car) {
        car.setPhotoUrl(photoUrl);
        Set<AppUser> owners = request.getOwner().stream()
                .map(userService::getUserById)
                .collect(Collectors.toSet());

        car.setOwners(owners);

        for (AppUser owner : owners) {
            owner.getCars().add(car);
        }
    }

    public void deleteCar(long id) {
        carRepository.deleteById(id);
    }

}
