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
import org.mockito.Spy;
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
import pleasework.kvira72.cars.user.UserService;
import pleasework.kvira72.cars.user.persistence.AppUser;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CarsServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private UserService userService;

    @Spy
    @InjectMocks
    private CarsService carsService;

    private Car car;
    private AppUser owner;
    private AppUser buyer;
    private final String mockToken = "Bearer mock.jwt.token";

    @BeforeEach
    void setUp() {
        owner = new AppUser();
        owner.setUsername("testUser");
        owner.setBalanceInCents(10000L);


        buyer = new AppUser();
        buyer.setUsername("buyer");
        buyer.setBalanceInCents(150000L);
        buyer.setBalanceInCents(20000L);

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
        car.setPriceInCents(5000L);
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
    void testListCarForSale_Success() throws ParseException, JsonProcessingException {
        doReturn("seller").when(carsService).getUsernameFromToken(mockToken);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(userService.getUser("seller")).thenReturn(owner);

        carsService.listCarForSale(1L, 5000L, mockToken);

        assertTrue(car.isForSale());
        assertEquals(5000L, car.getPriceInCents());
        verify(carRepository, times(1)).save(car);
    }


    @Test
    void testListCarForSale_ShouldThrowExceptionIfAlreadyForSale() {
        car.setForSale(true);
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        assertThrows(RuntimeException.class, () -> carsService.listCarForSale(1L, 60000L, "Bearer token"));
    }

    @Test
    void testPurchaseCar_ShouldThrowExceptionIfNotForSale() throws ParseException, JsonProcessingException {
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

    @Test
    void testPurchaseCar_Success() throws ParseException, JsonProcessingException {
        car.setForSale(true);
        doReturn("buyer").when(carsService).getUsernameFromToken(mockToken);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(userService.getUser("buyer")).thenReturn(buyer);

        carsService.purchaseCar(1L, mockToken);

        assertFalse(car.isForSale());
        assertTrue(car.getOwners().contains(buyer));
        assertEquals(15000L, owner.getBalanceInCents());
        assertEquals(15000L, buyer.getBalanceInCents());

        verify(carRepository, times(1)).save(car);
        verify(userService, times(1)).saveUser(buyer);
        verify(userService, times(1)).saveUser(owner);
    }

    @Test
    void testPurchaseCar_ShouldThrowExceptionIfInsufficientBalance() throws ParseException, JsonProcessingException {
        buyer.setBalanceInCents(0L);
        doReturn("buyer").when(carsService).getUsernameFromToken(mockToken);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(userService.getUser("buyer")).thenReturn(buyer);

        assertThrows(RuntimeException.class, () -> carsService.purchaseCar(1L, mockToken));
    }

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
