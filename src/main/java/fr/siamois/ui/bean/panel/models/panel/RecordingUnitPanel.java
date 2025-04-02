package fr.siamois.ui.bean.panel.models.panel;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.actionunit.ActionUnitFormMapping;
import fr.siamois.domain.models.exceptions.recordingunit.RecordingUnitNotFoundException;
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
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.panel.FlowBean;
import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import jakarta.faces.context.FacesContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.model.menu.DefaultMenuItem;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
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
public class RecordingUnitPanel extends AbstractPanel {

    // Deps
    private final LangBean langBean;
    private final SessionSettingsBean sessionSettingsBean;
    private final transient SpatialUnitService spatialUnitService;
    private final transient ActionUnitService actionUnitService;
    private final transient RecordingUnitService recordingUnitService;
    private final transient ConceptService conceptService;
    private final transient FieldConfigurationService fieldConfigurationService;
    private final FlowBean flowBean;

    // ------- Locals
    String recordingUnitErrorMessage;
    RecordingUnit recordingUnit;
    Long recordingUnitId;
    private LocalDate startDate;
    private LocalDate endDate;
    // Form
    private transient CustomForm additionalForm;


    public RecordingUnitPanel(LangBean langBean, SessionSettingsBean sessionSettingsBean, SpatialUnitService spatialUnitService, ActionUnitService actionUnitService, RecordingUnitService recordingUnitService, ConceptService conceptService, FieldConfigurationService fieldConfigurationService, FlowBean flowBean) {
        super("Unité d'enregistrement", "bi bi-pencil-square", "siamois-panel recording-unit-panel recording-unit-single-panel");
        this.langBean = langBean;
        this.sessionSettingsBean = sessionSettingsBean;
        this.spatialUnitService = spatialUnitService;
        this.actionUnitService = actionUnitService;
        this.recordingUnitService = recordingUnitService;
        this.conceptService = conceptService;
        this.fieldConfigurationService = fieldConfigurationService;
        this.flowBean = flowBean;
    }

    @Override
    public String display() {
        return "/panel/recordingUnitPanel.xhtml";
    }

    public LocalDate offsetDateTimeToLocalDate(OffsetDateTime offsetDT) {
        return offsetDT.toLocalDate();
    }

    void init() {

        try {


                this.recordingUnit = this.recordingUnitService.findById(recordingUnitId);
                if (this.recordingUnit.getStartDate() != null) {
                    this.startDate = offsetDateTimeToLocalDate(this.recordingUnit.getStartDate());
                }
                if (this.recordingUnit.getEndDate() != null) {
                    this.endDate = offsetDateTimeToLocalDate(this.recordingUnit.getEndDate());
                }




        } catch (RecordingUnitNotFoundException e) {
            recordingUnitErrorMessage = "Unable to get recording unit";
            log.error("Recording unit with ID {} not found", recordingUnitId);

        } catch (RuntimeException err) {
            recordingUnitErrorMessage = "Unable to get recording unit";

        }

        DefaultMenuItem item = DefaultMenuItem.builder()
                .value(recordingUnit.getFullIdentifier())
                .icon("bi bi-arrow-down-square")
                .build();
        this.getBreadcrumb().getModel().getElements().add(item);


    }


    public List<Concept> fetchChildrenOfConcept(Concept concept) {

        UserInfo info = sessionSettingsBean.getUserInfo();
        List<Concept> concepts ;

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


    public static class RecordingUnitPanelBuilder {

        private final RecordingUnitPanel recordingUnitPanel;

        public RecordingUnitPanelBuilder(ObjectProvider<RecordingUnitPanel> recordingUnitPanelProvider) {
            this.recordingUnitPanel = recordingUnitPanelProvider.getObject();
        }

        public RecordingUnitPanel.RecordingUnitPanelBuilder breadcrumb(PanelBreadcrumb breadcrumb) {
            recordingUnitPanel.setBreadcrumb(breadcrumb);

            return this;
        }


        public RecordingUnitPanel.RecordingUnitPanelBuilder id(Long id) {
            recordingUnitPanel.setRecordingUnitId(id);

            return this;
        }

        public RecordingUnitPanel build() {
            recordingUnitPanel.init();
            return recordingUnitPanel;
        }
    }
}
