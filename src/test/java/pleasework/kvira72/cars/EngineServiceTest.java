package pleasework.kvira72.cars;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pleasework.kvira72.cars.entity.EngineRepository;
import pleasework.kvira72.cars.persistence.EngineService;

@ExtendWith(MockitoExtension.class)
public class EngineServiceTest {

    @Mock
    private EngineRepository engineRepository;

    @InjectMocks
    private EngineService engineService;



}
