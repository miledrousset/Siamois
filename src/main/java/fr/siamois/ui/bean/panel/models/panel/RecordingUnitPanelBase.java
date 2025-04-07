package fr.siamois.ui.bean.panel.models.panel;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.actionunit.ActionUnitFormMapping;
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
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.panel.FlowBean;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@NoArgsConstructor(force = true)
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class RecordingUnitPanelBase extends AbstractPanel{

    // Deps
    protected final LangBean langBean;
    protected final SessionSettingsBean sessionSettingsBean;
    protected final transient SpatialUnitService spatialUnitService;
    protected final transient ActionUnitService actionUnitService;
    protected final transient RecordingUnitService recordingUnitService;
    protected final transient PersonService personService;
    protected final transient ConceptService conceptService;
    protected final transient FieldConfigurationService fieldConfigurationService;
    protected final FlowBean flowBean;

    // ---------- Locals
    // RU
    protected RecordingUnit recordingUnit;
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected Boolean hasSecondaryTypeOptions = false;
    protected Boolean hasThirdTypeOptions = false;
    // Form
    protected transient CustomForm additionalForm;

    protected RecordingUnitPanelBase(LangBean langBean,
                                     SessionSettingsBean sessionSettingsBean,
                                     SpatialUnitService spatialUnitService,
                                     ActionUnitService actionUnitService,
                                     RecordingUnitService recordingUnitService,
                                     PersonService personService, ConceptService conceptService,
                                     FieldConfigurationService fieldConfigurationService,
                                     FlowBean flowBean,
                                     String title, String icon, String panelClass) {

        super(title, icon, panelClass);
        this.langBean = langBean;
        this.sessionSettingsBean = sessionSettingsBean;
        this.spatialUnitService = spatialUnitService;
        this.actionUnitService = actionUnitService;
        this.recordingUnitService = recordingUnitService;
        this.personService = personService;
        this.conceptService = conceptService;
        this.fieldConfigurationService = fieldConfigurationService;
        this.flowBean = flowBean;
    }

    public LocalDate offsetDateTimeToLocalDate(OffsetDateTime offsetDT) {
        return offsetDT.toLocalDate();
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

    public void initFormResponseAnswers() {

        if (recordingUnit.getFormResponse().getForm() != null) {

            recordingUnit.getFormResponse().getForm().getLayout().stream()
                    .flatMap(section -> section.getFields().stream()) // Flatten the nested lists
                    .forEach(this::initializeAnswer); // Process each field
        }


    }

    public OffsetDateTime localDateToOffsetDateTime(LocalDate localDate) {
        return localDate.atTime(LocalTime.NOON).atOffset(ZoneOffset.UTC);
    }


}
