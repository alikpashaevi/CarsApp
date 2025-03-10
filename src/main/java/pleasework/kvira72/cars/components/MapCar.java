package pleasework.kvira72.cars.components;

import pleasework.kvira72.cars.persistence.Car;
import pleasework.kvira72.cars.error.NotFoundException;
import pleasework.kvira72.cars.model.CarDTO;
import pleasework.kvira72.cars.model.EngineDTO;
import pleasework.kvira72.cars.user.persistence.AppUser;

public class MapCar {
    public static CarDTO mapCar(Car car) {
        String ownerUsername = car.getOwners().stream().findFirst().map(AppUser::getUsername).orElse(new NotFoundException("Owner not found").getMessage());
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
}
