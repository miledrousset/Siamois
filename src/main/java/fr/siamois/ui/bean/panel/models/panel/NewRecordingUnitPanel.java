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
import fr.siamois.domain.models.recordingunit.RecordingUnitAltimetry;
import fr.siamois.domain.models.recordingunit.RecordingUnitSize;
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
import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
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
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static java.time.OffsetDateTime.now;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class NewRecordingUnitPanel extends RecordingUnitPanelBase {


    // ------- Locals
    Long actionUnitId;

    public NewRecordingUnitPanel(LangBean langBean, SessionSettingsBean sessionSettingsBean, SpatialUnitService spatialUnitService,
                                 ActionUnitService actionUnitService, RecordingUnitService recordingUnitService,
                                 PersonService personService, ConceptService conceptService,
                                 FieldConfigurationService fieldConfigurationService, FlowBean flowBean) {
        super(
                langBean,
                sessionSettingsBean,
                spatialUnitService,
                actionUnitService,
                recordingUnitService,
                personService,
                conceptService,
                fieldConfigurationService,
                flowBean,
                "Nouvelle unité d'enregistrement",
                "bi bi-pencil-square",
                "siamois-panel recording-unit-panel new-recording-unit-panel");
    }

    @Override
    public String display() {
        return "/panel/newRecordingUnitPanel.xhtml";
    }



    void init() {
        recordingUnit = new RecordingUnit();
        recordingUnit.setActionUnit(actionUnitService.findById(actionUnitId));
        recordingUnit.setCreatedByInstitution(recordingUnit.getActionUnit().getCreatedByInstitution());
        recordingUnit.setAuthor(sessionSettingsBean.getAuthenticatedUser());
        recordingUnit.setExcavator(sessionSettingsBean.getAuthenticatedUser());
        recordingUnit.setSize(new RecordingUnitSize());
        recordingUnit.setAltitude(new RecordingUnitAltimetry());
        this.startDate = offsetDateTimeToLocalDate(now());
        DefaultMenuItem item = DefaultMenuItem.builder()
                .value("Nouvelle unité d'enregistrement")
                .icon("bi bi-pencil-square")
                .build();
        this.getBreadcrumb().getModel().getElements().add(item);
    }




    public RecordingUnit save(RecordingUnit recordingUnit,
                              Concept typeConcept,
                              LocalDate startDate,
                              LocalDate endDate) {


        // handle dates
        if (startDate != null) {
            recordingUnit.setStartDate(localDateToOffsetDateTime(startDate));
        }
        if (endDate != null) {
            recordingUnit.setEndDate(localDateToOffsetDateTime(endDate));
        }

        return recordingUnitService.save(recordingUnit,
                typeConcept,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList());

    }

    public boolean save() {
        try {

            RecordingUnit saved = save(this.recordingUnit, recordingUnit.getType(), startDate, endDate);

            // Return page with id
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_INFO,
                            "Info",
                            langBean.msg("recordingunit.created", recordingUnit.getIdentifier())));

            Integer idx = flowBean.getPanels().indexOf(this);

            // If unable to find idx, we create new panel?

            // Remove last item from breadcrumb
            this.getBreadcrumb().getModel().getElements().remove(this.getBreadcrumb().getModel().getElements().size() - 1);
            flowBean.goToRecordingUnitByIdCurrentPanel(saved.getId(), idx);


            return true;

        } catch (RuntimeException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            langBean.msg("recordingunit.creationfailed", recordingUnit.getIdentifier()) + ": " + e.getMessage()
                    ));

            log.error("Error while saving: {}", e.getMessage());

        }
        return false;
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


    public List<Concept> completeRecordingUnitType(String input) {
        UserInfo info = sessionSettingsBean.getUserInfo();
        List<Concept> concepts = new ArrayList<>();
        try {
            concepts = fieldConfigurationService.fetchAutocomplete(info, RecordingUnit.TYPE_FIELD_CODE, input);
        } catch (NoConfigForFieldException e) {
            log.error(e.getMessage(), e);
        }
        return concepts;
    }

    public List<Person> completePerson(String query) {
        if (query == null || query.isEmpty()) {
            return Collections.emptyList();
        }
        query = query.toLowerCase();
        return personService.findAllByNameLastnameContaining(query);
    }


    public String getUrlForRecordingTypeFieldCode() {
        return fieldConfigurationService.getUrlForFieldCode(sessionSettingsBean.getUserInfo(), RecordingUnit.TYPE_FIELD_CODE);
    }


    public List<Concept> suggestValues(String query) {
        List<Concept> suggestions = new ArrayList<>();

        // Might be better to only pass question index
        FacesContext context = FacesContext.getCurrentInstance();
        CustomField question = (CustomField) UIComponent.getCurrentComponent(context).getAttributes().get("question");

        // Check if the question is an instance of QuestionSelectMultiple
        if (question instanceof CustomFieldSelectMultiple selectMultipleQuestion) {
            List<Concept> allOptions = new ArrayList<>(selectMultipleQuestion.getConcepts());

            // Filter the options based on user input (query)
            for (Concept value : allOptions) {
                if (value.getLabel().toLowerCase().contains(query.toLowerCase())) {
                    suggestions.add(value);
                }
            }
        }

        return suggestions;
    }
}
