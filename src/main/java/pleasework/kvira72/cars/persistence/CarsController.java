package pleasework.kvira72.cars.persistence;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pleasework.kvira72.cars.model.CarDTO;
import pleasework.kvira72.cars.model.CarRequest;

import java.util.List;

import static pleasework.kvira72.cars.sercurity.AuthorizationConstants.ADMIN;
import static pleasework.kvira72.cars.sercurity.AuthorizationConstants.USER_OR_ADMIN;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarsController {

    private final CarsService carsService;

    @GetMapping
    @PreAuthorize(USER_OR_ADMIN)
    Page<CarDTO> getCars(@RequestParam int page,
                         @RequestParam int pageSize) {
        return carsService.getCars(page, pageSize);
    }

    @GetMapping("/cars/for-sale")
    @PreAuthorize(USER_OR_ADMIN)
    public Page<CarDTO> getCarsForSale(@RequestParam int page,
                                       @RequestParam int pageSize) {
        return carsService.getCarsForSale(page, pageSize);
    }

    @PostMapping("/cars/{carId}/list-for-sale")
    @PreAuthorize(USER_OR_ADMIN)
    public ResponseEntity<String> listCarForSale(@PathVariable Long carId, @RequestParam Long ownerId) {
        try {
            carsService.listCarForSale(carId, ownerId);
            return ResponseEntity.ok("Car listed for sale successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/cars/{carId}/purchase")
    @PreAuthorize(USER_OR_ADMIN)
    public ResponseEntity<String> purchaseCar(@PathVariable Long carId, @RequestParam Long buyerId) {
        try {
            carsService.purchaseCar(carId, buyerId);
            return ResponseEntity.ok("Car purchased successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    @PreAuthorize(ADMIN)
    void addCar(@RequestBody @Valid CarRequest request) {
        carsService.addCar(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize(ADMIN)
    ResponseEntity<String> updateCar(@PathVariable long id, @RequestBody @Valid CarRequest request) {
        carsService.updateCar(id, request);
        return ResponseEntity.ok("Car updated successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(ADMIN)
    public ResponseEntity<String> deleteCar(@PathVariable long id) {
        carsService.deleteCar(id);
        return ResponseEntity.ok("Car deleted successfully");
    }

    @GetMapping("/{id}")
    @PreAuthorize(USER_OR_ADMIN)
    public ResponseEntity<Object> getCar(@PathVariable long id) {
        return ResponseEntity.ok(carsService.getCar(id));
    }

}
