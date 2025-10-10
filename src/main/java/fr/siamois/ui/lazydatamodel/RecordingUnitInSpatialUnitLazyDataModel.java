package fr.siamois.ui.lazydatamodel;

import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public class RecordingUnitInSpatialUnitLazyDataModel extends BaseRecordingUnitLazyDataModel {



    @Getter
    private final transient SpatialUnit spatialUnit;

    public RecordingUnitInSpatialUnitLazyDataModel(RecordingUnitService recordingUnitService,
                                                   LangBean langBean, SpatialUnit spatialUnit) {
        super(recordingUnitService,langBean);
        this.spatialUnit = spatialUnit;
    }

    @Override
    protected Page<RecordingUnit> loadRecordingUnits(String fullIdentifierFilter,
                                                     Long[] categoryIds,
                                                     Long[] personIds,
                                                     String globalFilter,
                                                     Pageable pageable) {
        return recordingUnitService.findAllByInstitutionAndBySpatialUnitAndByFullIdentifierContainingAndByCategoriesAndByGlobalContaining(
                spatialUnit.getId(),
                fullIdentifierFilter, categoryIds, globalFilter,
                langBean.getLanguageCode(),
                pageable
        );
    }



}
