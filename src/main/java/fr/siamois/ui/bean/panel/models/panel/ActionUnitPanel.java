package fr.siamois.ui.bean.panel.models.panel;

import fr.siamois.domain.models.actionunit.ActionCode;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.actionunit.ActionUnitNotFoundException;
import fr.siamois.domain.models.exceptions.vocabulary.NoConfigForFieldException;
import fr.siamois.domain.models.form.customfield.CustomField;
import fr.siamois.domain.models.form.customfield.CustomFieldSelectOneFromFieldCode;
import fr.siamois.domain.models.form.customfield.CustomFieldText;
import fr.siamois.domain.models.form.customfieldanswer.CustomFieldAnswer;
import fr.siamois.domain.models.form.customfieldanswer.CustomFieldAnswerSelectOneFromFieldCode;
import fr.siamois.domain.models.form.customfieldanswer.CustomFieldAnswerText;
import fr.siamois.domain.models.form.customform.CustomCol;
import fr.siamois.domain.models.form.customform.CustomFormPanel;
import fr.siamois.domain.models.form.customform.CustomRow;
import fr.siamois.domain.models.form.customformresponse.CustomFormResponse;
import fr.siamois.domain.models.history.ActionUnitHist;
import fr.siamois.domain.models.history.SpatialUnitHist;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.domain.services.vocabulary.FieldService;
import fr.siamois.domain.services.vocabulary.LabelService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.RedirectBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import fr.siamois.ui.bean.settings.team.TeamMembersBean;
import fr.siamois.ui.lazydatamodel.BaseLazyDataModel;
import fr.siamois.utils.DateUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.model.menu.DefaultMenuItem;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>This bean handles the spatial unit page</p>
 *
 * @author Grégory Bliault
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ActionUnitPanel extends AbstractSingleEntityPanel<ActionUnit, ActionUnitHist> implements Serializable {

    // Deps
    private final transient ActionUnitService actionUnitService;
    private final LangBean langBean;

    private final SessionSettingsBean sessionSettingsBean;
    private final transient FieldConfigurationService fieldConfigurationService;
    private final transient FieldService fieldService;
    private final RedirectBean redirectBean;
    private final transient LabelService labelService;
    private final TeamMembersBean teamMembersBean;


    // For entering new code
    private ActionCode newCode;
    private Integer newCodeIndex; // Index of the new code, if primary: 0, otherwise 1 to N
    // (but corresponds to 0 to N-1 in secondary code list)

    // Field related
    private Boolean editType;
    private Concept fType;

    // form
    private CustomFieldText nameField;
    private Concept nameConcept ;
    private CustomFieldSelectOneFromFieldCode typeField;
    private Concept actionUnitTypeConcept;

    private transient List<ActionCode> secondaryActionCodes;


    public ActionUnitPanel(ActionUnitService actionUnitService, LangBean langBean, SessionSettingsBean sessionSettingsBean, FieldConfigurationService fieldConfigurationService, FieldService fieldService, RedirectBean redirectBean, LabelService labelService, TeamMembersBean teamMembersBean) {
        super("Unité d'action", "bi bi-arrow-down-square", "siamois-panel action-unit-panel action-unit-single-panel");
        this.actionUnitService = actionUnitService;
        this.langBean = langBean;
        this.sessionSettingsBean = sessionSettingsBean;
        this.fieldConfigurationService = fieldConfigurationService;
        this.fieldService = fieldService;
        this.redirectBean = redirectBean;
        this.labelService = labelService;
        this.teamMembersBean = teamMembersBean;
    }

    @Override
    public String display() {
        return "/panel/actionUnitPanel.xhtml";
    }

    @Override
    public String ressourceUri() {
        return String.format("/actionunit/%s", unit.getId());
    }

    @Override
    protected BaseLazyDataModel<ActionUnit> getLazyDataModelChildren() {
        return null;
    }

    @Override
    public BaseLazyDataModel<ActionUnit> getLazyDataModelParents() {
        return null;
    }

    public void init() {

            // reinit
            errorMessage = null;
            unit = null;
            newCode = new ActionCode();
            secondaryActionCodes = new ArrayList<>();
            // Get the requested action from DB
            try {
                if (idunit != null) {
                    unit = actionUnitService.findById(idunit);
                    this.titleCodeOrTitle = unit.getName();
                    secondaryActionCodes = new ArrayList<>(unit.getSecondaryActionCodes());
                    fType = this.unit.getType();
                    DefaultMenuItem item = DefaultMenuItem.builder()
                            .value(unit.getName())
                            .icon("bi bi-arrow-down-square")
                            .build();
                    this.getBreadcrumb().getModel().getElements().add(item);
                } else {
                    log.error("The Action Unit page should not be accessed without ID or by direct page path");
                    redirectBean.redirectTo(HttpStatus.NOT_FOUND);
                }
            } catch (ActionUnitNotFoundException e) {
                log.error("Action unit with id {} not found", idunit);
                redirectBean.redirectTo(HttpStatus.NOT_FOUND);
            } catch (RuntimeException e) {
                this.errorMessage = "Failed to load action unit: " + e.getMessage();
                redirectBean.redirectTo(HttpStatus.INTERNAL_SERVER_ERROR);
            }




    }

    @Override
    public List<Person> authorsAvailable() {
        return List.of();
    }

    @Override
    public void initForms() {

        // Get from from DB in futur iteration

        // Init details tab form
        layout = new ArrayList<>();
        CustomFormPanel mainPanel = new CustomFormPanel();
        mainPanel.setIsSystemPanel(true);
        mainPanel.setName("common.header.general");
        // One row
        CustomRow row1 = new CustomRow();
        // Two cols

        CustomCol col1 = new CustomCol();
        nameField = new CustomFieldText();
        nameField.setIsSystemField(true);
        nameField.setLabel("spatialunit.field.name");
        col1.setField(nameField);
        col1.setClassName(COLUMN_CLASS_NAME);

        CustomCol col2 = new CustomCol();
        typeField = new CustomFieldSelectOneFromFieldCode();
        typeField.setLabel("spatialunit.field.type");
        typeField.setIsSystemField(true);
        typeField.setFieldCode(SpatialUnit.CATEGORY_FIELD_CODE);
        col2.setField(typeField);
        col2.setClassName(COLUMN_CLASS_NAME);

        row1.setColumns(List.of(col1, col2));
        mainPanel.setRows(List.of(row1));
        layout.add(mainPanel);

        // init overveiw tab form
        overviewLayout = new ArrayList<>();
        CustomFormPanel mainOverviewPanel = new CustomFormPanel();
        mainOverviewPanel.setIsSystemPanel(true);
        mainOverviewPanel.setName("common.header.general");
        // One row
        CustomRow row2 = new CustomRow();
        // one cols
        CustomCol col3 = new CustomCol();
        col3.setField(typeField);
        col3.setClassName(COLUMN_CLASS_NAME);
        row2.setColumns(List.of(col3));
        mainOverviewPanel.setRows(List.of(row2));
        overviewLayout.add(mainOverviewPanel);

        // Init form answers
        formResponse = new CustomFormResponse();
        Map<CustomField, CustomFieldAnswer> answers = new HashMap<>();
        CustomFieldAnswerText nameAnswer = new CustomFieldAnswerText();
        CustomFieldAnswerSelectOneFromFieldCode typeAnswer = new CustomFieldAnswerSelectOneFromFieldCode();
        nameAnswer.setValue(unit.getName());
        nameAnswer.setHasBeenModified(false);
        answers.put(nameField, nameAnswer);
        typeAnswer.setValue(unit.getType());
        typeAnswer.setHasBeenModified(false);
        answers.put(typeField, typeAnswer);
        formResponse.setAnswers(answers);

    }

    @Override
    public void cancelChanges() {
        unit.setName(backupClone.getName());
        unit.setValidated(backupClone.getValidated());
        unit.setType(backupClone.getType());
        hasUnsavedModifications = false;
        initForms();
    }

    @Override
    public void saveDocument() {

    }

    @Override
    public void save(Boolean validated) {

    }


    public String formatDate(OffsetDateTime offsetDateTime) {
        return DateUtils.formatOffsetDateTime(offsetDateTime);
    }

    public void saveNewActionCode() {
        // Update the action code
        if (newCodeIndex == 0) {
            // update primary action code
            unit.setPrimaryActionCode(newCode);
        } else if (newCodeIndex > 0) {
            unit.getSecondaryActionCodes().add(newCode);
            secondaryActionCodes.set(newCodeIndex - 1, newCode);
        }
    }



    /**
     * Fetch the autocomplete results for the action codes
     *
     * @param input the input of the user
     * @return the list of codes the input to display in the autocomplete
     */
    public List<ActionCode> completeActionCode(String input) {

        return actionUnitService.findAllActionCodeByCodeIsContainingIgnoreCase(input);

    }

    public String getUrlForActionCodeTypeFieldCode() {
        return fieldConfigurationService.getUrlForFieldCode(sessionSettingsBean.getUserInfo(), ActionCode.TYPE_FIELD_CODE);
    }

    /**
     * Fetch the autocomplete results on API for the action code type field
     *
     * @param input the input of the user
     * @return the list of concepts that match the input to display in the autocomplete
     */
    public List<Concept> completeActionCodeType(String input) {

        try {
            return fieldConfigurationService.fetchAutocomplete(sessionSettingsBean.getUserInfo(), ActionCode.TYPE_FIELD_CODE, input);
        } catch (NoConfigForFieldException e) {
            log.error(e.getMessage(), e);
            return new ArrayList<>();
        }

    }

    public void handleSelectPrimaryCode() {
        // To implement
    }

    public void addNewSecondaryCode() {
        ActionCode code = new ActionCode();
        Concept c = new Concept();
        code.setCode("");
        code.setType(c);
        secondaryActionCodes.add(code);
    }

    public void initNewActionCode(int index) {
        newCodeIndex = index;
        newCode = new ActionCode();
    }

    public void removeSecondaryCode(int index) {
        secondaryActionCodes.remove(index);
    }

    public String getFormattedValue(Object value) {
        if (value == null) {
            return "";
        }

        if (value instanceof Number) {
            // Integer or Number case
            return value.toString();
        } else if (value instanceof List<?> list) {
            // Handle list of concepts
            String langCode = sessionSettingsBean.getLanguageCode();
            return list.stream()
                    .map(item -> (item instanceof Concept concept) ? labelService.findLabelOf(concept, langCode).getValue() : item.toString())
                    .collect(Collectors.joining(", "));
        }

        return value.toString(); // Default case
    }






    public static class ActionUnitPanelBuilder {

        private final ActionUnitPanel actionUnitPanel;

        public ActionUnitPanelBuilder(ObjectProvider<ActionUnitPanel> actionUnitPanelProvider) {
            this.actionUnitPanel = actionUnitPanelProvider.getObject();
        }

        public ActionUnitPanelBuilder id(Long id) {
            actionUnitPanel.setIdunit(id);
            return this;
        }

        public ActionUnitPanelBuilder breadcrumb(PanelBreadcrumb breadcrumb) {
            actionUnitPanel.setBreadcrumb(breadcrumb);

            return this;
        }



        public ActionUnitPanel build() {
            actionUnitPanel.init();
            return actionUnitPanel;
        }
    }

    public void goToMemberList() {
        redirectBean.redirectTo(String.format("/settings/organisation/actionunit/%s/members", unit.getId()));
    }

}