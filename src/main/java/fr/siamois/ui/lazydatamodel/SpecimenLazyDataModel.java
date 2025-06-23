package fr.siamois.ui.lazydatamodel;

import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.specimen.Specimen;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.domain.services.specimen.SpecimenService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public class SpecimenLazyDataModel extends BaseSpecimenLazyDataModel {

    private final transient SpecimenService specimenService;
    private final transient SessionSettingsBean sessionSettings;

    public SpecimenLazyDataModel(
                                 SpecimenService specimenService,
                                 SessionSettingsBean sessionSettings,
                                 LangBean langBean) {
        super(specimenService, langBean);
        this.specimenService = specimenService;
        this.sessionSettings = sessionSettings;
    }

    @Override
    protected Page<Specimen> loadSpecimens(String fullIdentifierFilter,
                                           Long[] categoryIds,
                                           Long[] personIds,
                                           String globalFilter,
                                           Pageable pageable) {
        return specimenService.findAllByInstitutionAndByFullIdentifierContainingAndByCategoriesAndByGlobalContaining(
                sessionSettings.getSelectedInstitution().getId(),
                fullIdentifierFilter, categoryIds, globalFilter,
                langBean.getLanguageCode(),
                pageable
        );
    }



}
