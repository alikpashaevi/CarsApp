package pleasework.kvira72.cars.user;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pleasework.kvira72.cars.error.NotFoundException;
import pleasework.kvira72.cars.user.model.UserRequest;
import pleasework.kvira72.cars.user.persistence.AppUser;
import pleasework.kvira72.cars.user.persistence.AppUserRepository;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public void createUser(UserRequest request) {
        AppUser user = new AppUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(request.getRoleIds().stream()
                .map(roleService::getRole)
                .collect(Collectors.toSet()));

        repository.save(user);
    }

    public AppUser getUser(String username) {
        return repository.findByUsername(username).orElseThrow(() -> new NotFoundException("User with username " + username + " not found"));
    }
}
