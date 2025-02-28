package pleasework.kvira72.cars.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pleasework.kvira72.cars.entity.Car;
import pleasework.kvira72.cars.model.CarDTO;
import pleasework.kvira72.cars.user.model.UserRequest;
import pleasework.kvira72.cars.user.persistence.AppUser;

import java.util.List;
import java.util.Set;

import static pleasework.kvira72.cars.sercurity.AuthorizationConstants.ADMIN;
import static pleasework.kvira72.cars.sercurity.AuthorizationConstants.USER_OR_ADMIN;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    public final UserService userService;

    @PostMapping
    @PreAuthorize(ADMIN)
    public void createUser(@RequestBody @Valid UserRequest request) {
        userService.createUser(request);
    }

    @GetMapping
    @PreAuthorize(ADMIN)
    public ResponseEntity<Set<AppUser>> getUsers() {
        Set<AppUser> users = userService.getUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{username}")
    @PreAuthorize(ADMIN)
    public ResponseEntity<AppUser> getUser(@PathVariable String username) {
        AppUser user = userService.getUser(username);
        return ResponseEntity.ok(user);
    }

}
