package pleasework.kvira72.cars.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pleasework.kvira72.cars.model.CarDTO;
import pleasework.kvira72.cars.model.CarRequest;
import pleasework.kvira72.cars.persistence.aws.S3Service;
import software.amazon.awssdk.http.HttpStatusCode;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import static pleasework.kvira72.cars.sercurity.AuthorizationConstants.ADMIN;
import static pleasework.kvira72.cars.sercurity.AuthorizationConstants.USER_OR_ADMIN;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarsController {

    private final CarsService carsService;

    private final S3Service s3Service;

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

    @GetMapping("/search")
    @PreAuthorize(USER_OR_ADMIN)
    public Page<CarDTO> searchCars(@RequestParam String model,
                                   @RequestParam int page,
                                   @RequestParam int pageSize) {
        return carsService.searchCars(model, page, pageSize);
    }

    @PostMapping("/cars/{carId}/list-for-sale")
    @PreAuthorize(USER_OR_ADMIN)
    public ResponseEntity<String> listCarForSale(@PathVariable Long carId, @RequestParam Long priceInCents, @RequestHeader("Authorization") String token) {
        try {
            carsService.listCarForSale(carId, priceInCents, token);
            return ResponseEntity.ok("Car listed for sale successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ParseException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/cars/{carId}/purchase")
    @PreAuthorize(USER_OR_ADMIN)
    public ResponseEntity<String> purchaseCar(@PathVariable Long carId, @RequestHeader("Authorization") String token) {
        try {
            carsService.purchaseCar(carId, token);
            return ResponseEntity.ok("Car purchased successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ParseException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping
    @PreAuthorize(ADMIN)
    ResponseEntity<Void> addCar(@RequestPart("photo") MultipartFile photo, @RequestPart("car") @Valid CarRequest request) throws IOException {
        String photoUrl = s3Service.uploadFile(
                "car-photos/" + photo.getOriginalFilename(),
                photo.getInputStream(),
                photo.getSize()
        );

        carsService.addCar(request, photoUrl);
        return ResponseEntity.status(HttpStatusCode.CREATED).build();
    }



    @PutMapping("/{id}")
    @PreAuthorize(ADMIN)
    ResponseEntity<String> updateCar(@PathVariable Long id, @RequestPart("photo") MultipartFile photo, @RequestPart("car") @Valid CarRequest request) throws IOException {
        String photoUrl = s3Service.uploadFile(
                "car-photos/" + photo.getOriginalFilename(),
                photo.getInputStream(),
                photo.getSize()
        );
        carsService.updateCar(id, request, photoUrl);
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
