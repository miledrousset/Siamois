package fr.siamois.ui.model;

import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class SpatialUnitLazyDataModel extends BaseSpatialUnitLazyDataModel {

    private final transient SpatialUnitService spatialUnitService;
    private final transient SessionSettingsBean sessionSettings;
    private final transient LangBean langBean;

    public SpatialUnitLazyDataModel(SpatialUnitService spatialUnitService, SessionSettingsBean sessionSettings, LangBean langBean) {
        this.spatialUnitService = spatialUnitService;
        this.sessionSettings = sessionSettings;
        this.langBean = langBean;
    }

    @Override
    protected Page<SpatialUnit> loadSpatialUnits(String nameFilter, Long[] categoryIds, String globalFilter, Pageable pageable) {
        return spatialUnitService.findAllByInstitutionAndByNameContainingAndByCategoriesAndByGlobalContaining(
                sessionSettings.getSelectedInstitution().getId(),
                nameFilter, categoryIds, globalFilter,
                langBean.getLanguageCode(),
                pageable);
    }
}
