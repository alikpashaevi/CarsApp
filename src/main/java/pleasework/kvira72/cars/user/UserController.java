package pleasework.kvira72.cars.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pleasework.kvira72.cars.user.model.UserRequest;

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

}
