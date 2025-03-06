package pleasework.kvira72.cars.persistence;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pleasework.kvira72.cars.entity.Engine;
import pleasework.kvira72.cars.model.EngineDTO;
import pleasework.kvira72.cars.model.EngineRequest;

import java.util.List;

import static pleasework.kvira72.cars.sercurity.AuthorizationConstants.ADMIN;
import static pleasework.kvira72.cars.sercurity.AuthorizationConstants.USER_OR_ADMIN;

@RestController
@RequestMapping("/engines")
@RequiredArgsConstructor
public class EngineController {

    private final EngineService engineService;

    @GetMapping
    @PreAuthorize(USER_OR_ADMIN)
    Page<EngineDTO> getEngines(@RequestParam int page, @RequestParam int pageSize, @RequestParam(required = false) Double capacity) {
        return engineService.getEngines(page, pageSize, capacity);
    }

    @GetMapping("/{id}")
    @PreAuthorize(USER_OR_ADMIN)
    Engine getEngine(@PathVariable Long id) {
        return engineService.findEngine(id);
    }

    @PostMapping
    @PreAuthorize(ADMIN)
    ResponseEntity<Void> createEngine(@RequestBody @Valid EngineRequest request) {
        engineService.createEngine(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize(ADMIN)
    EngineDTO updateEngine(@PathVariable Long id, @RequestBody @Valid EngineRequest request) {
        return engineService.updateEngine(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(ADMIN)
    ResponseEntity<String> deleteEngine(@PathVariable Long id) {
        engineService.deleteEngine(id);
        return ResponseEntity.ok("Engine deleted successfully");
    }
}
