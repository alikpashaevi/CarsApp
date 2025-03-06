package pleasework.kvira72.cars.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
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

import java.text.ParseException;
import java.util.Map;
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
                        car.getEngine().getCapacity()),
                car.getPhotoUrl());
    }

    private NotFoundException buildNotFoundException(long id) {
        return new NotFoundException("Car with id " + id + " not found");
    }

    public String getUsernameFromToken(String token) throws ParseException, JsonProcessingException {
        SignedJWT signedJWT = SignedJWT.parse(token.substring(7));

        String payload = signedJWT.getPayload().toString();

        ObjectMapper objectMapper = new ObjectMapper();
        Map payloadMap = objectMapper.readValue(payload, Map.class);

        return (String) payloadMap.get("sub");
    }

    public Page<CarDTO> getCars(int page, int pageSize) {
        return carRepository.findCars(PageRequest.of(page, pageSize));
    }

    public CarDTO getCar(long id) {
        Car car = carRepository.findById(id).orElseThrow(() -> buildNotFoundException(id));
        return mapCar(car);
    }

    @Transactional
    public void listCarForSale(Long carId, Long priceInCents, String token) throws ParseException, JsonProcessingException {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> buildNotFoundException(carId));

        if (car.isForSale()) {
            throw new RuntimeException("Car is already for sale");
        }
        String username = getUsernameFromToken(token);

        AppUser owner = userService.getUser(username);

        if (!car.getOwners().contains(owner)) {
            throw new RuntimeException("You are not the owner of this car");
        }
        car.setPriceInCents(priceInCents);
        car.setForSale(true);
        carRepository.save(car);
    }

    @Transactional
    public void purchaseCar(Long carId, String token) throws ParseException, JsonProcessingException {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found"));

        String username = getUsernameFromToken(token);

        AppUser buyer = userService.getUser(username);

        System.out.println("Username: " + username);

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

        Set<AppUser> owners = car.getOwners();
        AppUser seller = owners.iterator().next();

        buyer.setBalanceInCents(buyer.getBalanceInCents() - car.getPriceInCents());

        seller.setBalanceInCents(seller.getBalanceInCents() + car.getPriceInCents());

        car.removeOwner(seller);
        car.addOwner(buyer);

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
