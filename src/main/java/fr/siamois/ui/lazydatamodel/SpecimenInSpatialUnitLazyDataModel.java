package fr.siamois.ui.lazydatamodel;

import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.specimen.Specimen;
import fr.siamois.domain.services.specimen.SpecimenService;
import fr.siamois.ui.bean.LangBean;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public class SpecimenInSpatialUnitLazyDataModel extends BaseSpecimenLazyDataModel {



    // locals
    @Getter
    private final transient SpatialUnit spatialUnit;

    public SpecimenInSpatialUnitLazyDataModel(
            SpecimenService specimenService,
            LangBean langBean, SpatialUnit spatialUnit) {
        super(specimenService, langBean);
        this.spatialUnit = spatialUnit;
    }

    @Override
    protected Page<Specimen> loadSpecimens(String fullIdentifierFilter,
                                           Long[] categoryIds,
                                           Long[] personIds,
                                           String globalFilter,
                                           Pageable pageable) {
        return specimenService.findAllBySpatialUnitAndByFullIdentifierContainingAndByCategoriesAndByGlobalContaining(
                spatialUnit.getId(),
                fullIdentifierFilter, categoryIds, globalFilter,
                langBean.getLanguageCode(),
                pageable
        );
    }



}
