package pleasework.kvira72.cars.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pleasework.kvira72.cars.user.model.AppUserDTO;
import pleasework.kvira72.cars.user.model.UserRequest;

import java.util.Set;

import static pleasework.kvira72.cars.sercurity.AuthorizationConstants.ADMIN;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize(ADMIN)
public class UserController {

    public final UserService userService;

    @PostMapping
    public void createUser(@RequestBody @Valid UserRequest request) {
        userService.createUser(request);
    }

    @GetMapping
    public ResponseEntity<Set<AppUserDTO>> getUsers() {
        Set<AppUserDTO> users = userService.getUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{username}")
    public ResponseEntity<AppUserDTO> getUser(@PathVariable String username) {
        AppUserDTO user = userService.getUserDTO(username);
        return ResponseEntity.ok(user);
    }

}
