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
