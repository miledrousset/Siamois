package fr.siamois.ui.lazydatamodel;

import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public class RecordingUnitLazyDataModel extends BaseRecordingUnitLazyDataModel {

    private final transient RecordingUnitService recordingUnitService;
    private final transient SessionSettingsBean sessionSettings;
    private final transient LangBean langBean;

    public RecordingUnitLazyDataModel(RecordingUnitService recordingUnitService, SessionSettingsBean sessionSettings, LangBean langBean) {
        this.recordingUnitService = recordingUnitService;
        this.sessionSettings = sessionSettings;
        this.langBean = langBean;
    }

    @Override
    protected Page<RecordingUnit> loadRecordingUnits(String fullIdentifierFilter,
                                                     Long[] categoryIds,
                                                     Long[] personIds,
                                                     String globalFilter,
                                                     Pageable pageable) {
        return recordingUnitService.findAllByInstitutionAndByFullIdentifierContainingAndByCategoriesAndByGlobalContaining(
                sessionSettings.getSelectedInstitution().getId(),
                fullIdentifierFilter, categoryIds, globalFilter,
                langBean.getLanguageCode(),
                pageable
        );
    }



}
