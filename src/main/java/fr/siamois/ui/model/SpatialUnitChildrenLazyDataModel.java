package fr.siamois.ui.model;

import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.ui.bean.LangBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class SpatialUnitChildrenLazyDataModel extends BaseSpatialUnitLazyDataModel {

    private final transient SpatialUnitService spatialUnitService;
    private final transient LangBean langBean;
    private final transient SpatialUnit spatialUnit;

    public SpatialUnitChildrenLazyDataModel(SpatialUnitService spatialUnitService
            , LangBean langBean
            , SpatialUnit spatialUnit) {
        this.spatialUnitService = spatialUnitService;
        this.langBean = langBean;
        this.spatialUnit = spatialUnit;
    }

    @Override
    protected Page<SpatialUnit> loadSpatialUnits(String nameFilter, Long[] categoryIds, Long[] personIds, String globalFilter, Pageable pageable) {
        return spatialUnitService.findAllByParentAndByNameContainingAndByCategoriesAndByGlobalContaining(
                spatialUnit,
                nameFilter, categoryIds, personIds, globalFilter,
                langBean.getLanguageCode(),
                pageable);
    }
}
