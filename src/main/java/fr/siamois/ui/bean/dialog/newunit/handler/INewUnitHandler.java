package fr.siamois.ui.bean.dialog.newunit.handler;

import fr.siamois.domain.models.TraceableEntity;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.exceptions.EntityAlreadyExistsException;
import fr.siamois.domain.models.form.customform.CustomForm;
import fr.siamois.ui.bean.dialog.newunit.GenericNewUnitDialogBean;
import fr.siamois.ui.bean.dialog.newunit.UnitKind;


public interface INewUnitHandler<T extends TraceableEntity> {
    UnitKind kind();
    T newEmpty();
    T save(UserInfo user, T unit) throws EntityAlreadyExistsException;
    String dialogWidgetVar();
    String successMessageCode();              // ex. "common.entity.spatialUnits.updated"
    String viewUrlFor(Long id);               // ex. "/spatial-unit/%d" ou "/action-unit/%d"
    CustomForm formLayout();            // ton objet/form layout (SpatialUnit.NEW_UNIT_FORM, etc.)
    void onInitFromContext(GenericNewUnitDialogBean<?> bean); // optionnel pour pré-remplir (parents/enfants…)
    String getName(T unit); // Get unit name

    // shared UI defaults (pull from UnitKind)
    default String getResourceUri()     { return kind().resourceUri(); }
    default String getTitle()           { return kind().title(); }
    default String styleClassName()     { return kind().styleClass(); }
    default String getIcon()            { return kind().icon(); }
    default String getAutocompleteClass(){ return kind().autocompleteClass(); }
}

