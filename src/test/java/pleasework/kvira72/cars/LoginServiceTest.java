package pleasework.kvira72.cars;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pleasework.kvira72.cars.auth.LoginRequest;
import pleasework.kvira72.cars.auth.LoginResponse;
import pleasework.kvira72.cars.auth.LoginService;
import pleasework.kvira72.cars.error.InvalidLoginException;
import pleasework.kvira72.cars.error.NotFoundException;
import pleasework.kvira72.cars.user.UserService;
import pleasework.kvira72.cars.user.persistence.AppUser;

import java.lang.reflect.Field;
import java.text.ParseException;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @InjectMocks
    private LoginService loginService;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AppUser mockUser;

    private static final String SECRET_KEY = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    @BeforeEach
    void setUp() throws Exception {
        mockUser = new AppUser();
        mockUser.setUsername("testuser");
        mockUser.setPassword("encodedPassword");

        Field secretKeyField = LoginService.class.getDeclaredField("secretKey");
        secretKeyField.setAccessible(true);
        secretKeyField.set(loginService, SECRET_KEY);
    }

    @Test
    void login_ValidCredentials_ReturnsToken() throws Exception {
        LoginRequest request = new LoginRequest("testuser", "password123");
        when(userService.getUser(request.getUsername())).thenReturn(mockUser);
        when(passwordEncoder.matches(request.getPassword(), mockUser.getPassword())).thenReturn(true);

        LoginResponse response = loginService.login(request);

        assertNotNull(response);
        assertNotNull(response.getAccessToken());

        SignedJWT signedJWT = SignedJWT.parse(response.getAccessToken());
        assertEquals("testuser", signedJWT.getJWTClaimsSet().getSubject());

        assertTrue(signedJWT.verify(new MACVerifier(SECRET_KEY.getBytes())));
    }

    @Test
    void login_InvalidPassword_ThrowsInvalidLoginException() {
        LoginRequest request = new LoginRequest("testuser", "wrongpassword");
        when(userService.getUser(request.getUsername())).thenReturn(mockUser);
        when(passwordEncoder.matches(request.getPassword(), mockUser.getPassword())).thenReturn(false);

        assertThrows(InvalidLoginException.class, () -> loginService.login(request));
    }

    @Test
    void login_UserNotFound_ThrowsNotFoundException() {
        LoginRequest request = new LoginRequest("nonexistent", "password");
        when(userService.getUser(request.getUsername())).thenThrow(new NotFoundException("User not found"));

        assertThrows(NotFoundException.class, () -> loginService.login(request));
    }
}
