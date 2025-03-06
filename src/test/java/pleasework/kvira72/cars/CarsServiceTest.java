package pleasework.kvira72.cars;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import pleasework.kvira72.cars.entity.Car;
import pleasework.kvira72.cars.entity.CarRepository;
import pleasework.kvira72.cars.entity.Engine;
import pleasework.kvira72.cars.error.NotFoundException;
import pleasework.kvira72.cars.model.CarDTO;
import pleasework.kvira72.cars.persistence.CarsService;
import pleasework.kvira72.cars.persistence.EngineService;
import pleasework.kvira72.cars.user.UserService;
import pleasework.kvira72.cars.user.persistence.AppUser;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class CarsServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private EngineService engineService;

    @Mock
    private UserService userService;

    @InjectMocks
    private CarsService carsService;

    private Car car;
    private AppUser owner;

    @BeforeEach
    void setUp() {
        owner = new AppUser();
        owner.setUsername("testUser");
        owner.setBalanceInCents(100000L);

        Engine engine = new Engine();
        engine.setId(1L);
        engine.setHorsePower(200);
        engine.setCapacity(2.0);

        car = new Car();
        car.setId(1L);
        car.setModel("Test Model");
        car.setYear(2022);
        car.setDriveable(true);
        car.setForSale(false);
        car.setPriceInCents(50000);
        car.setEngine(engine);
        car.setOwners(new HashSet<>(Collections.singletonList(owner)));
    }

    @Test
    void testMapCar() {
        CarDTO carDTO = carsService.mapCar(car);

        assertNotNull(carDTO);
        assertEquals(car.getId(), carDTO.getId());
        assertEquals(car.getModel(), carDTO.getModel());
        assertEquals(car.getYear(), carDTO.getYear());
        assertEquals(owner.getUsername(), carDTO.getOwner());
    }

    @Test
    void testGetCar_ShouldReturnCarDTO() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        CarDTO result = carsService.getCar(1L);

        assertNotNull(result);
        assertEquals(car.getId(), result.getId());
        verify(carRepository, times(1)).findById(1L);
    }

    @Test
    void testGetCar_ShouldThrowExceptionWhenCarNotFound() {
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> carsService.getCar(1L));
    }

    @Test
    void testListCarForSale_ShouldThrowExceptionIfAlreadyForSale() throws ParseException, JsonProcessingException {
        car.setForSale(true);
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        assertThrows(RuntimeException.class, () -> carsService.listCarForSale(1L, 60000L, "Bearer token"));
    }

    @Test
    void testPurchaseCar_ShouldThrowExceptionIfNotForSale() throws ParseException, JsonProcessingException {
        // Arrange
        Long carId = 1L;
        String token = "Bearer mockToken";
        Car car = new Car();
        car.setId(carId);
        car.setForSale(false);

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));

        CarsService spyService = Mockito.spy(carsService);
        doReturn("mockUsername").when(spyService).getUsernameFromToken(anyString());

        assertThrows(RuntimeException.class, () -> spyService.purchaseCar(carId, token));
    }

    // TODO: Debug it

    @Test
    void testGetCars_ShouldReturnPagedResults() {
        CarDTO carDTO = carsService.mapCar(car);
        PageImpl<CarDTO> carPage = new PageImpl<>(Collections.singletonList(carDTO));
        when(carRepository.findCars(PageRequest.of(0, 10))).thenReturn(carPage);

        Page<CarDTO> result = carsService.getCars(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testDeleteCar_ShouldCallRepositoryDeleteById() {
        doNothing().when(carRepository).deleteById(1L);

        carsService.deleteCar(1L);

        verify(carRepository, times(1)).deleteById(1L);
    }
}
