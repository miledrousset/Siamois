package fr.siamois.ui.bean.panel.models.panel.single;

import fr.siamois.domain.services.spatialunit.SpatialUnitService;
import fr.siamois.domain.services.spatialunit.SpatialUnitTreeService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.ui.bean.SessionSettingsBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class PanelsConfig {
    @Bean
    AbstractSingleEntity.Deps singleEntityDeps(
            SessionSettingsBean sessionSettingsBean,
            FieldConfigurationService fieldConfigurationService,
            SpatialUnitTreeService spatialUnitTreeService,
            SpatialUnitService spatialUnitService
    ) {
        return new AbstractSingleEntity.Deps(
                sessionSettingsBean, fieldConfigurationService, spatialUnitTreeService, spatialUnitService
        );
    }
}
