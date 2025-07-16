package fr.siamois.ui.bean.panel.models.panel.single;

import fr.siamois.domain.models.actionunit.ActionCode;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.actionunit.ActionUnitNotFoundException;
import fr.siamois.domain.models.exceptions.recordingunit.FailedRecordingUnitSaveException;
import fr.siamois.domain.models.exceptions.vocabulary.NoConfigForFieldException;
import fr.siamois.domain.models.form.customfield.CustomField;
import fr.siamois.domain.models.form.customfield.CustomFieldSelectOneFromFieldCode;
import fr.siamois.domain.models.form.customfield.CustomFieldText;
import fr.siamois.domain.models.form.customfieldanswer.CustomFieldAnswer;
import fr.siamois.domain.models.form.customfieldanswer.CustomFieldAnswerSelectOneFromFieldCode;
import fr.siamois.domain.models.form.customfieldanswer.CustomFieldAnswerText;
import fr.siamois.domain.models.form.customform.CustomCol;
import fr.siamois.domain.models.form.customform.CustomForm;
import fr.siamois.domain.models.form.customform.CustomFormPanel;
import fr.siamois.domain.models.form.customform.CustomRow;
import fr.siamois.domain.models.form.customformresponse.CustomFormResponse;
import fr.siamois.domain.models.history.ActionUnitHist;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.HistoryService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.document.DocumentService;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.domain.services.spatialunit.SpatialUnitTreeService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.domain.services.vocabulary.FieldService;
import fr.siamois.domain.services.vocabulary.LabelService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.RedirectBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.dialog.document.DocumentCreationBean;
import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import fr.siamois.ui.bean.settings.team.TeamMembersBean;
import fr.siamois.ui.lazydatamodel.BaseLazyDataModel;
import fr.siamois.ui.lazydatamodel.RecordingUnitInActionUnitLazyDataModel;
import fr.siamois.utils.MessageUtils;
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
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Slf4j
@Data
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ActionUnitPanel extends AbstractSingleEntityPanel<ActionUnit, ActionUnitHist> implements Serializable {

    // Deps
    private final transient ActionUnitService actionUnitService;
    private final LangBean langBean;

    private final transient FieldService fieldService;
    private final RedirectBean redirectBean;
    private final transient LabelService labelService;
    private final TeamMembersBean teamMembersBean;
    private final transient HistoryService historyService;
    private final transient DocumentService documentService;
    private final transient RecordingUnitService recordingUnitService;

    // For entering new code
    private ActionCode newCode;
    private Integer newCodeIndex; // Index of the new code, if primary: 0, otherwise 1 to N
    // (but corresponds to 0 to N-1 in secondary code list)

    // Field related
    private Boolean editType;
    private Concept fType;

    // form
    private CustomFieldText nameField;
    private Concept nameConcept;
    private CustomFieldSelectOneFromFieldCode typeField;
    private Concept actionUnitTypeConcept;

    private transient List<ActionCode> secondaryActionCodes;

    // Linked recording units
    private transient RecordingUnitInActionUnitLazyDataModel recordingUnitListLazyDataModel ;


    public ActionUnitPanel(ActionUnitService actionUnitService, LangBean langBean,
                           SessionSettingsBean sessionSettingsBean,
                           FieldConfigurationService fieldConfigurationService,
                           FieldService fieldService, RedirectBean redirectBean,
                           LabelService labelService, TeamMembersBean teamMembersBean,
                           HistoryService historyService, DocumentService documentService, RecordingUnitService recordingUnitService,
                           DocumentCreationBean documentCreationBean,
                           SpatialUnitTreeService spatialUnitTreeService) {
        super("Unité d'action", "bi bi-arrow-down-square", "siamois-panel action-unit-panel action-unit-single-panel",
                documentCreationBean, sessionSettingsBean, fieldConfigurationService, spatialUnitTreeService);
        this.actionUnitService = actionUnitService;
        this.langBean = langBean;
        this.fieldService = fieldService;
        this.redirectBean = redirectBean;
        this.labelService = labelService;
        this.teamMembersBean = teamMembersBean;
        this.historyService = historyService;
        this.documentService = documentService;
        this.recordingUnitService = recordingUnitService;
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

    public void refreshUnit() {

        // reinit
        hasUnsavedModifications = false;
        errorMessage = null;
        unit = null;
        newCode = new ActionCode();
        secondaryActionCodes = new ArrayList<>();

        try {

            unit = actionUnitService.findById(idunit);
            this.setTitleCodeOrTitle(unit.getName()); // Set panel title
            backupClone = new ActionUnit(unit);
            this.titleCodeOrTitle = unit.getName();
            secondaryActionCodes = new ArrayList<>(unit.getSecondaryActionCodes());
            fType = this.unit.getType();

            initForms();


            // Get all the CHILDREN of the spatial unit
            selectedCategoriesChildren = new ArrayList<>();
            totalChildrenCount = 0;
            // Get all the Parentsof the spatial unit
            selectedCategoriesParents = new ArrayList<>();
            totalParentsCount = 0;


        } catch (RuntimeException e) {
            this.errorMessage = "Failed to load action unit: " + e.getMessage();
        }


        historyVersion = historyService.findActionUnitHistory(unit);
        documents = documentService.findForActionUnit(unit);
    }

    @Override
    public void init() {
        try {
            activeTabIndex = 0;


            nameConcept = new Concept();
            nameConcept.setExternalId("SYSTEM_NAME");
            nameConcept.setVocabulary(SYSTEM_THESO);


            if (idunit == null) {
                this.errorMessage = "The ID of the spatial unit must be defined";
                return;
            }

            refreshUnit();

            if (this.unit == null) {
                log.error("The Action Unit page should not be accessed without ID or by direct page path");
                errorMessage = "The Action Unit page should not be accessed without ID or by direct page path";
            }

            recordingUnitListLazyDataModel = new RecordingUnitInActionUnitLazyDataModel(
                    recordingUnitService,
                    sessionSettingsBean,
                    langBean,
                    unit
            );
            recordingUnitListLazyDataModel.setSelectedUnits(new ArrayList<>());

            // add to BC
            DefaultMenuItem item = DefaultMenuItem.builder()
                    .value(unit.getName())
                    .icon("bi bi-arrow-down-square")
                    .build();
            this.getBreadcrumb().getModel().getElements().add(item);

        } catch (
                ActionUnitNotFoundException e) {
            log.error("Action unit with id {} not found", idunit);
            redirectBean.redirectTo(HttpStatus.NOT_FOUND);
        } catch (
                RuntimeException e) {
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
        typeField.setFieldCode(ActionUnit.TYPE_FIELD_CODE);
        col2.setField(typeField);
        col2.setClassName(COLUMN_CLASS_NAME);

        row1.setColumns(List.of(col1, col2));
        mainPanel.setRows(List.of(row1));


        // init overveiw tab form

        CustomFormPanel mainOverviewPanel = new CustomFormPanel();
        mainOverviewPanel.setIsSystemPanel(true);
        mainOverviewPanel.setName("common.header.general");
        // One row
        CustomRow row2 = new CustomRow();
        // one cols
        CustomCol col3 = new CustomCol();
        col3.setReadOnly(true);
        col3.setField(typeField);
        col3.setClassName(COLUMN_CLASS_NAME);
        row2.setColumns(List.of(col3));
        mainOverviewPanel.setRows(List.of(row2));

        overviewForm = new CustomForm.Builder()
                .name("Overview tab form")
                .description("Contains the overview")
                .addPanel(mainOverviewPanel)
                .build();
        detailsForm = new CustomForm.Builder()
                .name("Overview tab form")
                .description("Contains the overview")
                .addPanel(mainPanel)
                .build();

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
    public void visualise(ActionUnitHist history) {
        // TODO: implement
    }

    @Override
    public void saveDocument() {
        // TODO : implement
    }

    @Override
    public void save(Boolean validated) {

        // Recupération des champs systeme

        // Name
        CustomFieldAnswerText nameAnswer = (CustomFieldAnswerText) formResponse.getAnswers().get(nameField);
        CustomFieldAnswerSelectOneFromFieldCode typeAnswer = (CustomFieldAnswerSelectOneFromFieldCode) formResponse.getAnswers().get(typeField);
        unit.setName(nameAnswer.getValue());
        unit.setType(typeAnswer.getValue());

        unit.setValidated(validated);
        try {
            actionUnitService.save(unit);
        }
        catch(FailedRecordingUnitSaveException e) {
            MessageUtils.displayErrorMessage(langBean, "common.entity.spatialUnits.updateFailed", unit.getName());
            return ;
        }

        refreshUnit();
        MessageUtils.displayInfoMessage(langBean, "common.entity.spatialUnits.updated", unit.getName());
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

    @Override
    public String displayHeader() {
        return "/panel/header/actionUnitPanelHeader.xhtml";
    }

    public void addNewSecondaryCode() {
        ActionCode code = new ActionCode();
        Concept c = new Concept();
        code.setCode("");
        code.setType(c);
        secondaryActionCodes.add(code);
    }

    @Override
    public String getAutocompleteClass() {
        return "action-unit-autocomplete";
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