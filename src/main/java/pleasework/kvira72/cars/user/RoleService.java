package pleasework.kvira72.cars.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pleasework.kvira72.cars.error.NotFoundException;
import pleasework.kvira72.cars.user.persistence.Role;
import pleasework.kvira72.cars.user.persistence.RoleRepository;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role getRole(Long id) {
        return roleRepository.findById(id).orElseThrow(() -> new NotFoundException("Role with id " + id + " not found"));
    }

}
