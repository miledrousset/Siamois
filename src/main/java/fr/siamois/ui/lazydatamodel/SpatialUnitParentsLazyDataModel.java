package fr.siamois.ui.lazydatamodel;

import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.services.spatialunit.SpatialUnitService;
import fr.siamois.ui.bean.LangBean;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class SpatialUnitParentsLazyDataModel extends BaseSpatialUnitLazyDataModel {

    private final transient SpatialUnitService spatialUnitService;
    private final transient LangBean langBean;

    @Getter
    private final transient SpatialUnit spatialUnit;

    public SpatialUnitParentsLazyDataModel(SpatialUnitService spatialUnitService
            , LangBean langBean
            , SpatialUnit spatialUnit) {
        this.spatialUnitService = spatialUnitService;
        this.langBean = langBean;

        this.spatialUnit = spatialUnit;
    }

    @Override
    protected Page<SpatialUnit> loadSpatialUnits(String nameFilter, Long[] categoryIds, Long[] personIds, String globalFilter, Pageable pageable) {
        return spatialUnitService.findAllByChildAndByNameContainingAndByCategoriesAndByGlobalContaining(
                spatialUnit,
                nameFilter, categoryIds, personIds, globalFilter,
                langBean.getLanguageCode(),
                pageable);
    }
}
