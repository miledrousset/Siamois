package fr.siamois.config;

import fr.siamois.infrastructure.repositories.ActionUnitRepository;
import fr.siamois.infrastructure.repositories.RecordingUnitRepository;
import fr.siamois.infrastructure.repositories.SpatialUnitRepository;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestRepositoryConfig {

    @Bean
    public SpatialUnitRepository spatialUnitRepository() {
        return Mockito.mock(SpatialUnitRepository.class);
    }

    @Bean
    public RecordingUnitRepository recordingUnitRepository() {
        return Mockito.mock(RecordingUnitRepository.class);
    }
    @Bean
    public ActionUnitRepository actionUnitRepository() {
        return Mockito.mock(ActionUnitRepository.class);
    }

}
