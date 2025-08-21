package fr.siamois.ui.bean.dialog.newunit;

import fr.siamois.domain.models.TraceableEntity;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.form.customform.CustomForm;


public interface INewUnitHandler<T extends TraceableEntity> {
    UnitKind kind();
    T newEmpty();
    T save(UserInfo user, T unit) throws Exception;
    String dialogWidgetVar();                 // ex. "newSpatialUnitDiag" / "newActionUnitDiag"
    String successMessageCode();              // ex. "common.entity.spatialUnits.updated"
    String viewUrlFor(Long id);               // ex. "/spatial-unit/%d" ou "/action-unit/%d"
    CustomForm formLayout();            // ton objet/form layout (SpatialUnit.NEW_UNIT_FORM, etc.)
    void onInitFromContext(GenericNewUnitDialogBean<?> bean); // optionnel pour pré-remplir (parents/enfants…)
}

