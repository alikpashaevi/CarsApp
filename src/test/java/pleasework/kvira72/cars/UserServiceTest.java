package pleasework.kvira72.cars;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pleasework.kvira72.cars.error.NotFoundException;
import pleasework.kvira72.cars.user.RoleService;
import pleasework.kvira72.cars.user.UserService;
import pleasework.kvira72.cars.user.model.UserRequest;
import pleasework.kvira72.cars.user.persistence.AppUser;
import pleasework.kvira72.cars.user.persistence.AppUserRepository;
import pleasework.kvira72.cars.user.persistence.Role;

import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private AppUserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private UserService userService;

    private AppUser user;

    @BeforeEach
    void setUp() {
        user = new AppUser();
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setBalanceInCents(1000000L);
        user.setRoles(Set.of(new Role()));
    }

    @Test
    void testCreateUser() {
        UserRequest request = new UserRequest("testuser", "password", 1000000L, Set.of(1L));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(roleService.getRole(1L)).thenReturn(new Role());

        userService.createUser(request);

        verify(repository, times(1)).save(any(AppUser.class));
    }

    @Test
    void testGetUser() {
        when(repository.findByUsername("testuser")).thenReturn(Optional.of(user));

        AppUser result = userService.getUser("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void testGetUser_NotFound() {
        when(repository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUser("testuser"));
    }

    @Test
    void testSaveUser() {
        userService.saveUser(user);

        verify(repository, times(1)).save(user);
    }
}