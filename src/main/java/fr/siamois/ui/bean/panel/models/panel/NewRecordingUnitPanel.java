package fr.siamois.ui.bean.panel.models.panel;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.actionunit.ActionUnitFormMapping;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.vocabulary.NoConfigForFieldException;
import fr.siamois.domain.models.form.customfield.CustomField;
import fr.siamois.domain.models.form.customfield.CustomFieldInteger;
import fr.siamois.domain.models.form.customfield.CustomFieldSelectMultiple;
import fr.siamois.domain.models.form.customfieldanswer.CustomFieldAnswerInteger;
import fr.siamois.domain.models.form.customfieldanswer.CustomFieldAnswerSelectMultiple;
import fr.siamois.domain.models.form.customform.CustomForm;
import fr.siamois.domain.models.form.customformresponse.CustomFormResponse;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.panel.FlowBean;
import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.model.menu.DefaultMenuItem;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static java.time.OffsetDateTime.now;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class NewRecordingUnitPanel extends AbstractPanel {

    // Deps
    private final LangBean langBean;
    private final SessionSettingsBean sessionSettingsBean;
    private final transient SpatialUnitService spatialUnitService;
    private final transient ActionUnitService actionUnitService;
    private final transient ConceptService conceptService;
    private final transient FieldConfigurationService fieldConfigurationService;
    private final FlowBean flowBean;

    // ------- Locals
    RecordingUnit recordingUnit;
    Long actionUnitId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean hasSecondaryTypeOptions = false;
    private Boolean hasThirdTypeOptions = false;
    // Form
    private transient CustomForm additionalForm;


    public NewRecordingUnitPanel(LangBean langBean, SessionSettingsBean sessionSettingsBean, SpatialUnitService spatialUnitService, ActionUnitService actionUnitService, ConceptService conceptService, FieldConfigurationService fieldConfigurationService, FlowBean flowBean) {
        super("Nouvelle unité d'enregistrement", "bi bi-pencil-square", "siamois-panel recording-unit-panel new-recording-unit-panel");
        this.langBean = langBean;
        this.sessionSettingsBean = sessionSettingsBean;
        this.spatialUnitService = spatialUnitService;
        this.actionUnitService = actionUnitService;
        this.conceptService = conceptService;
        this.fieldConfigurationService = fieldConfigurationService;
        this.flowBean = flowBean;
    }

    @Override
    public String display() {
        return "/panel/newRecordingUnitPanel.xhtml";
    }

    public LocalDate offsetDateTimeToLocalDate(OffsetDateTime offsetDT) {
        return offsetDT.toLocalDate();
    }

    void init() {
        recordingUnit = new RecordingUnit();
        recordingUnit.setActionUnit(actionUnitService.findById(actionUnitId));
        recordingUnit.setCreatedByInstitution(recordingUnit.getActionUnit().getCreatedByInstitution());
        recordingUnit.setAuthor(sessionSettingsBean.getAuthenticatedUser());
        recordingUnit.setExcavator(sessionSettingsBean.getAuthenticatedUser());
        this.startDate = offsetDateTimeToLocalDate(now());
        DefaultMenuItem item = DefaultMenuItem.builder()
                .value("Nouvelle unité d'enregistrement")
                .icon("bi bi-pencil-square")
                .build();
        this.getBreadcrumb().getModel().getElements().add(item);
    }


    public List<Concept> fetchChildrenOfConcept(Concept concept) {

        UserInfo info = sessionSettingsBean.getUserInfo();
        List<Concept> concepts = new ArrayList<>();

        concepts = conceptService.findDirectSubConceptOf(info, concept);

        return concepts;

    }

    public void initializeAnswer(CustomField field) {
        if (recordingUnit.getFormResponse().getAnswers().get(field) == null) {
            // Init missing parameters
            if (field instanceof CustomFieldSelectMultiple) {
                recordingUnit.getFormResponse().getAnswers().put(field, new CustomFieldAnswerSelectMultiple());
            } else if (field instanceof CustomFieldInteger) {
                recordingUnit.getFormResponse().getAnswers().put(field, new CustomFieldAnswerInteger());
            }
        }
    }

    public void initFormResponseAnswers() {

        if (recordingUnit.getFormResponse().getForm() != null) {

            recordingUnit.getFormResponse().getForm().getLayout().stream()
                    .flatMap(section -> section.getFields().stream()) // Flatten the nested lists
                    .forEach(this::initializeAnswer); // Process each field
        }


    }

    public void changeCustomForm() {
        // Do we have a form available for the selected type?
        Set<ActionUnitFormMapping> formsAvailable = recordingUnit.getActionUnit().getFormsAvailable();
        additionalForm = getFormForRecordingUnitType(recordingUnit.getType(), formsAvailable);
        if (recordingUnit.getFormResponse() == null) {
            recordingUnit.setFormResponse(new CustomFormResponse());
            recordingUnit.getFormResponse().setAnswers(new HashMap<>());
        }
        recordingUnit.getFormResponse().setForm(additionalForm);
        if (additionalForm != null) {
            initFormResponseAnswers();
        }


    }

    public CustomForm getFormForRecordingUnitType(Concept type, Set<ActionUnitFormMapping> availableForms) {
        return availableForms.stream()
                .filter(mapping -> mapping.getPk().getConcept().equals(type) // Vérifier le concept
                        && "RECORDING_UNIT".equals(mapping.getPk().getTableName())) // Vérifier le tableName
                .map(mapping -> mapping.getPk().getForm())
                .findFirst()
                .orElse(null); // Retourner null si aucun match
    }

    public void handleSelectType() {

        if (recordingUnit.getType() != null) {
            hasSecondaryTypeOptions = !(this.fetchChildrenOfConcept(recordingUnit.getType()).isEmpty());
            changeCustomForm();
        } else {
            hasSecondaryTypeOptions = false;
        }

        recordingUnit.setSecondaryType(null);
        recordingUnit.setThirdType(null);
        hasThirdTypeOptions = false;


    }


    public void handleSelectSecondaryType() {

        if (recordingUnit.getSecondaryType() != null) {
            hasThirdTypeOptions = !(this.fetchChildrenOfConcept(recordingUnit.getSecondaryType()).isEmpty());
        } else {
            hasThirdTypeOptions = false;
        }

        recordingUnit.setThirdType(null);
    }

    public List<Concept> completeRecordingUnitSecondaryType(String input) {

        // The main type needs to be set
        if (recordingUnit.getType() == null) {
            return new ArrayList<>();
        }

        UserInfo info = sessionSettingsBean.getUserInfo();

        return fieldConfigurationService.fetchConceptChildrenAutocomplete(info, recordingUnit.getType(), input);

    }

    public String getUrlForRecordingSecondaryType() {
        if (recordingUnit.getType() != null) {
            return fieldConfigurationService.getUrlOfConcept(recordingUnit.getType());
        }
        return null;

    }

    public List<Concept> completeRecordingUnitThirdType(String input) {

        // The main type needs to be set
        if (recordingUnit.getSecondaryType() == null) {
            return new ArrayList<>();
        }

        UserInfo info = sessionSettingsBean.getUserInfo();

        return fieldConfigurationService.fetchConceptChildrenAutocomplete(info, recordingUnit.getSecondaryType(), input);

    }

    public String getUrlForRecordingThirdType() {
        if (recordingUnit.getSecondaryType() != null) {
            return fieldConfigurationService.getUrlOfConcept(recordingUnit.getSecondaryType());
        }
        return null;
    }

    public static class NewRecordingUnitPanelBuilder {

        private final NewRecordingUnitPanel newRecordingUnitPanel;

        public NewRecordingUnitPanelBuilder(ObjectProvider<NewRecordingUnitPanel> newRecordingUnitPanelProvider) {
            this.newRecordingUnitPanel = newRecordingUnitPanelProvider.getObject();
        }

        public NewRecordingUnitPanel.NewRecordingUnitPanelBuilder breadcrumb(PanelBreadcrumb breadcrumb) {
            newRecordingUnitPanel.setBreadcrumb(breadcrumb);

            return this;
        }

        public NewRecordingUnitPanel.NewRecordingUnitPanelBuilder actionUnitId(Long id) {
            newRecordingUnitPanel.setActionUnitId(id);

            return this;
        }

        public NewRecordingUnitPanel build() {
            newRecordingUnitPanel.init();
            return newRecordingUnitPanel;
        }
    }
}
