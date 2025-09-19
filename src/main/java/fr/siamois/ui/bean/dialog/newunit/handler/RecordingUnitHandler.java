package fr.siamois.ui.bean.dialog.newunit.handler;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.exceptions.EntityAlreadyExistsException;
import fr.siamois.domain.models.form.customform.CustomForm;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.ui.bean.dialog.newunit.GenericNewUnitDialogBean;
import fr.siamois.ui.bean.dialog.newunit.UnitKind;
import org.springframework.stereotype.Component;

@Component
public class RecordingUnitHandler implements INewUnitHandler<RecordingUnit> {

    private final RecordingUnitService recordingUnitService;

    public RecordingUnitHandler(RecordingUnitService recordingUnitService) {
        this.recordingUnitService = recordingUnitService;
    }

    @Override public UnitKind kind() { return UnitKind.RECORDING; }
    @Override public RecordingUnit newEmpty() { return new RecordingUnit(); }
    @Override public RecordingUnit save(UserInfo u, RecordingUnit unit) throws EntityAlreadyExistsException { return (RecordingUnit) recordingUnitService.save(unit); }
    @Override public String dialogWidgetVar() { return "newUnitDiag"; }
    @Override public String successMessageCode() { return "common.entity.recordingUnits.updated"; }
    @Override public String viewUrlFor(Long id) { return "/recording-unit/" + id; }
    @Override public CustomForm formLayout() { return RecordingUnit.NEW_UNIT_FORM; }
    @Override public void onInitFromContext(GenericNewUnitDialogBean<?> bean) { /* parents/enfants si besoin */ }

    @Override
    public String getName(RecordingUnit unit) {
        return unit.getFullIdentifier();
    }


}
