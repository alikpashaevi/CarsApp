package pleasework.kvira72.cars;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import pleasework.kvira72.cars.entity.Engine;
import pleasework.kvira72.cars.entity.EngineRepository;
import pleasework.kvira72.cars.model.EngineDTO;
import pleasework.kvira72.cars.model.EngineRequest;
import pleasework.kvira72.cars.persistence.EngineService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EngineServiceTest {

    @Mock
    private EngineRepository engineRepository;

    @InjectMocks
    private EngineService engineService;

    @Test
    void testGetEngines() {
        // Given
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<EngineDTO> enginePage = new PageImpl<>(List.of(new EngineDTO(1L, 100, 2.0)));
        when(engineRepository.findEngines(2.0, pageRequest)).thenReturn(enginePage);

        // When
        Page<EngineDTO> result = engineService.getEngines(0, 10, 2.0);

        // Then
        assertEquals(1,result.getContent().size());
        assertEquals(2.0, result.getContent().getFirst().getCapacity());
        verify(engineRepository).findEngines(2.0, pageRequest);
    }

    @Test
    void testCreateEngine() {
        // Given
        EngineRequest request = new EngineRequest(200, 2.0);
        when(engineRepository.save(any(Engine.class))).thenReturn(buildEngine());

        // When
        engineService.createEngine(request);

        // Then
        verify(engineRepository).save(any(Engine.class));

    }

    @Test
    void testUpdateEngine() {
        // Given
        EngineRequest request = new EngineRequest(200, 2.0);

        when(engineRepository.findById(1L)).thenReturn(java.util.Optional.of(buildEngine()));
        when(engineRepository.save(any(Engine.class))).thenReturn(buildEngine());

        // When
        EngineDTO result = engineService.updateEngine(1L, request);

        // Then
        assertEquals(200, result.getHorsePower());
        assertEquals(2.0, result.getCapacity());
        verify(engineRepository).findById(1L);
        verify(engineRepository).save(any(Engine.class));
    }

    @Test
    void testDeleteEngine() {
        engineService.deleteEngine(1L);

        verify(engineRepository).deleteById(1L);
    }

    @Test
    void testFindEngine() {
        when(engineRepository.findById(1L)).thenReturn(java.util.Optional.of(buildEngine()));

        Engine result = engineService.findEngine(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(engineRepository).findById(1L);
    }

    private Engine buildEngine() {
        Engine engine = new Engine();
        engine.setId(1L);
        engine.setCapacity(2.0);
        engine.setHorsePower(1500);
        return engine;
    }


}
