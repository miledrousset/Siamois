package fr.siamois.ui.lazydatamodel;

import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public class ActionUnitInSpatialUnitLazyDataModel extends BaseActionUnitLazyDataModel {

    private final transient ActionUnitService actionUnitService;
    private final transient SessionSettingsBean sessionSettings;
    private final transient LangBean langBean;
    private final transient SpatialUnit spatialUnit;

    public ActionUnitInSpatialUnitLazyDataModel(ActionUnitService actionUnitService, SessionSettingsBean sessionSettings, LangBean langBean, SpatialUnit spatialUnit) {
        this.actionUnitService = actionUnitService;
        this.sessionSettings = sessionSettings;
        this.langBean = langBean;
        this.spatialUnit = spatialUnit;
    }

    @Override
    protected Page<ActionUnit> loadActionUnits(String nameFilter, Long[] categoryIds, Long[] personIds, String globalFilter, Pageable pageable) {
        return actionUnitService.findAllByInstitutionAndBySpatialUnitAndByNameContainingAndByCategoriesAndByGlobalContaining(
                sessionSettings.getSelectedInstitution().getId(),
                spatialUnit.getId(),
                nameFilter, categoryIds, personIds, globalFilter,
                langBean.getLanguageCode(),
                pageable);
    }



}
