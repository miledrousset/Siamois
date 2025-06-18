package fr.siamois.ui.lazydatamodel;

import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public class RecordingUnitInActionUnitLazyDataModel extends BaseRecordingUnitLazyDataModel {

    private final transient RecordingUnitService recordingUnitService;
    private final transient SessionSettingsBean sessionSettings;
    private final transient LangBean langBean;
    @Getter
    private final transient ActionUnit actionUnit;

    public RecordingUnitInActionUnitLazyDataModel(RecordingUnitService recordingUnitService,
                                                  SessionSettingsBean sessionSettings,
                                                  LangBean langBean, ActionUnit actionUnit) {
        super();
        this.recordingUnitService = recordingUnitService;
        this.sessionSettings = sessionSettings;
        this.langBean = langBean;
        this.actionUnit = actionUnit;
    }

    @Override
    protected Page<RecordingUnit> loadRecordingUnits(String fullIdentifierFilter,
                                                     Long[] categoryIds,
                                                     Long[] personIds,
                                                     String globalFilter,
                                                     Pageable pageable) {
        return recordingUnitService.findAllByInstitutionAndByActionUnitAndByFullIdentifierContainingAndByCategoriesAndByGlobalContaining(
                sessionSettings.getSelectedInstitution().getId(),
                actionUnit.getId(),
                fullIdentifierFilter, categoryIds, globalFilter,
                langBean.getLanguageCode(),
                pageable
        );
    }



}
