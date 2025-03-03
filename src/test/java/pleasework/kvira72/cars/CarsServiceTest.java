package pleasework.kvira72.cars;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pleasework.kvira72.cars.entity.Car;
import pleasework.kvira72.cars.entity.CarRepository;
import pleasework.kvira72.cars.persistence.CarsService;
import pleasework.kvira72.cars.user.UserService;
import pleasework.kvira72.cars.user.persistence.AppUser;

import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class CarsServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CarsService carsService;

    private Car car;
    private AppUser buyer;
    private AppUser seller;

    @BeforeEach
    public void setUp() {
        // Initialize test data
        seller = new AppUser();
        seller.setId(1L);
        seller.setUsername("seller");
        seller.setBalanceInCents(500000L); // $5000.00

        buyer = new AppUser();
        buyer.setId(2L);
        buyer.setUsername("buyer");
        buyer.setBalanceInCents(300000L); // $3000.00

        car = new Car();
        car.setId(1L);
        car.setPriceInCents(200000L); // $2000.00
        car.setForSale(true);
        car.setOwners(Set.of(seller));
    }

    @Test
    public void testPurchaseCar_Success() throws Exception {
        // Arrange
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(userService.getUser("buyer")).thenReturn(buyer);

        // Act
        carsService.purchaseCar(1L, "valid_token");

        // Assert
        // Verify the car is no longer for sale
        assertFalse(car.isForSale());

        // Verify the buyer is now the owner
        assertTrue(car.getOwners().contains(buyer));
        assertFalse(car.getOwners().contains(seller));

        // Verify balances are updated
        assertEquals(100000L, buyer.getBalanceInCents()); // $3000 - $2000 = $1000
        assertEquals(700000L, seller.getBalanceInCents()); // $5000 + $2000 = $7000

        // Verify repository and service calls
        verify(carRepository, times(1)).save(car);
        verify(userService, times(1)).saveUser(buyer);
        verify(userService, times(1)).saveUser(seller);
    }

    @Test
    public void testPurchaseCar_InsufficientBalance() throws Exception {
        // Arrange
        buyer.setBalanceInCents(100000L); // $1000.00 (not enough to buy a $2000 car)
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(userService.getUser("buyer")).thenReturn(buyer);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            carsService.purchaseCar(1L, "valid_token");
        });

        assertEquals("Insufficient balance", exception.getMessage());

        // Verify no changes were made
        verify(carRepository, never()).save(car);
        verify(userService, never()).saveUser(any());
    }

    @Test
    public void testPurchaseCar_UserIsOwner() throws Exception {
        // Arrange
        car.setOwners(Set.of(buyer)); // Buyer is already the owner
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(userService.getUser("buyer")).thenReturn(buyer);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            carsService.purchaseCar(1L, "valid_token");
        });

        assertEquals("You are the owner of this car", exception.getMessage());

        // Verify no changes were made
        verify(carRepository, never()).save(car);
        verify(userService, never()).saveUser(any());
    }

    @Test
    public void testPurchaseCar_VerifySalePriceAndBalances() throws Exception {
        // Arrange
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(userService.getUser("buyer")).thenReturn(buyer);

        // Act
        carsService.purchaseCar(1L, "valid_token");

        // Assert
        // Verify the car's sale price is deducted from the buyer's balance
        assertEquals(100000L, buyer.getBalanceInCents()); // $3000 - $2000 = $1000

        // Verify the car's sale price is added to the seller's balance
        assertEquals(700000L, seller.getBalanceInCents()); // $5000 + $2000 = $7000
    }
}
