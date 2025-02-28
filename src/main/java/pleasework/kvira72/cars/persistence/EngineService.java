package pleasework.kvira72.cars.persistence;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pleasework.kvira72.cars.entity.Engine;
import pleasework.kvira72.cars.entity.EngineRepository;
import pleasework.kvira72.cars.error.NotFoundException;
import pleasework.kvira72.cars.model.EngineDTO;
import pleasework.kvira72.cars.model.EngineRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EngineService {

    private final EngineRepository engineRepository;

    private NotFoundException buildNotFoundException(long id) {
        return new NotFoundException("Engine with id " + id + " not found");
    }
    
    public Engine findEngine(Long id) {
        return engineRepository.findById(id).orElseThrow(() -> buildNotFoundException(id));
    }

    public Page<EngineDTO> getEngines(int page, int pageSize, double capacity) {
        return engineRepository.findEngines(capacity, PageRequest.of(page, pageSize));
    }


    public void createEngine(EngineRequest request) {
        Engine engine = new Engine();
        engine.setCapacity(request.getCapacity());
        engine.setHorsePower(request.getHorsePower());

        engineRepository.save(engine);
    }

    public EngineDTO updateEngine(Long id, EngineRequest request) {
        Engine engine = engineRepository.findById(id).orElseThrow(() -> buildNotFoundException(id));
        engine.setHorsePower(request.getHorsePower());
        engine.setCapacity(request.getCapacity());
        engineRepository.save(engine);

        return mapEngine(engine);
    }

    public void deleteEngine(Long id) {
        engineRepository.deleteById(id);
    }

    private EngineDTO mapEngine(Engine engine) {
        return new EngineDTO(
                engine.getId(),
                engine.getHorsePower(),
                engine.getCapacity());
    }
}
