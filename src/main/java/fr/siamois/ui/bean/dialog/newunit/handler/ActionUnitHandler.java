package fr.siamois.ui.bean.dialog.newunit.handler;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.exceptions.EntityAlreadyExistsException;
import fr.siamois.domain.models.form.customform.CustomForm;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.ui.bean.dialog.newunit.GenericNewUnitDialogBean;
import fr.siamois.ui.bean.dialog.newunit.UnitKind;
import org.springframework.stereotype.Component;

@Component
public class ActionUnitHandler implements INewUnitHandler<ActionUnit> {

    private final ActionUnitService actionUnitService;

    public ActionUnitHandler(ActionUnitService actionUnitService) {
        this.actionUnitService = actionUnitService;
    }

    @Override public UnitKind kind() { return UnitKind.ACTION; }
    @Override public ActionUnit newEmpty() { return new ActionUnit(); }
    @Override public ActionUnit save(UserInfo u, ActionUnit unit) throws EntityAlreadyExistsException { return actionUnitService.save(u, unit, unit.getType()); }
    @Override public String dialogWidgetVar() { return "newUnitDiag"; }
    @Override public String successMessageCode() { return "common.entity.spatialUnits.updated"; }
    @Override public String viewUrlFor(Long id) { return "/action-unit/" + id; }
    @Override public CustomForm formLayout() { return ActionUnit.NEW_UNIT_FORM; }
    @Override public void onInitFromContext(GenericNewUnitDialogBean<?> bean) { /* parents/enfants si besoin */ }

    @Override
    public String getName(ActionUnit unit) {
        return unit.getName();
    }

    @Override
    public String getRessourceUri() {
        return "/action-unit/new";
    }

    @Override
    public String getTitle() {
        return "Nouvelle unité d'action";
    }

    @Override
    public String styleClassName() {
        return "action-unit-panel";
    }

    @Override
    public String getIcon() {
        return "bi bi-pencil-square";
    }

    @Override
    public String getAutocompleteClass() {
        return "action-unit-autocomplete";
    }

}
