package fr.siamois.ui.bean.recordingunit;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.actionunit.ActionUnitFormMapping;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.recordingunit.RecordingUnitNotFoundException;
import fr.siamois.domain.models.exceptions.vocabulary.NoConfigForFieldException;
import fr.siamois.domain.models.form.customform.CustomForm;
import fr.siamois.domain.models.form.customfield.CustomFieldInteger;
import fr.siamois.domain.models.form.customfieldanswer.CustomFieldAnswerInteger;
import fr.siamois.domain.models.form.customfieldanswer.CustomFieldAnswerSelectMultiple;
import fr.siamois.domain.models.form.customfield.CustomField;
import fr.siamois.domain.models.form.customfield.CustomFieldSelectMultiple;
import fr.siamois.domain.models.form.customformresponse.CustomFormResponse;
import fr.siamois.domain.models.history.RecordingUnitHist;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.recordingunit.RecordingUnitAltimetry;
import fr.siamois.domain.models.recordingunit.RecordingUnitSize;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.HistoryService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.domain.services.recordingunit.StratigraphicRelationshipService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.domain.services.vocabulary.FieldService;
import fr.siamois.domain.services.form.FormService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.RedirectBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.event.SelectEvent;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static java.time.OffsetDateTime.now;

@Data
@Slf4j
@SessionScoped
@Component
public class NewRecordingUnitFormBean implements Serializable {

    // Deps
    private final transient RecordingUnitService recordingUnitService;
    private final transient ActionUnitService actionUnitService;
    private final transient HistoryService historyService;
    private final transient PersonService personService;
    private final transient FieldService fieldService;
    private final transient FormService formService;
    private final LangBean langBean;
    private final transient ConceptService conceptService;
    private final SessionSettingsBean sessionSettingsBean;
    private final transient FieldConfigurationService fieldConfigurationService;
    private final transient StratigraphicRelationshipService stratigraphicRelationshipService;
    private final RedirectBean redirectBean;

    // Local
    private RecordingUnit recordingUnit;
    private String recordingUnitErrorMessage;
    private LocalDate startDate;
    private LocalDate endDate;

    private Boolean isLocalisationFromSIG;


    private transient List<Concept> concepts;
    private Concept fType = null;
    private Concept previousFType = null;
    private Concept fSecondaryType = null;
    private Concept previousSecondaryFType = null;
    private Boolean hasSecondaryTypeOptions = false;
    private Concept fThirdType = null;
    private Boolean hasThirdTypeOptions = false;

    // View param
    private Long id;  // ID of the requested RU

    // History
    private transient List<RecordingUnitHist> historyVersion;
    private RecordingUnitHist revisionToDisplay = null;

    // Form
    private transient CustomForm additionalForm;

    // Stratigraphy
    private transient List<Event> events; // Strati
    private transient List<StratigraphyValidationEvent> validationEvents; // Strati
    private transient List<RecordingUnit> recordingUnitList; // To store the candidate for stratigraphic relationships
    private transient List<RecordingUnit> stratigraphySelectedRecordingUnit;
    private static final int POSTERIOR = 0;
    private static final int SYNCHRONOUS = 1;
    private static final int ANTERIOR = 2;
    private static final int CERTAIN = 0;
    private static final int UNCERTAIN = 1;
    private int stratiDialogType; // Index from 0 to 2
    private int stratiDialogCertainty; // 0 or 1
    private List<RecordingUnit> stratiDialogSelection;

    @Data
    public static class Event {

        private String status;
        private String date;
        private String icon;
        private String color;
        private String image;
        private int type;
        private List<RecordingUnit> recordingUnitList;

        public Event(String status, String date, String icon, String color) {
            this.status = status;
            this.date = date;
            this.icon = icon;
            this.color = color;
        }

        public Event(String status, String date, String icon, String color, String image) {
            this.status = status;
            this.date = date;
            this.icon = icon;
            this.color = color;
            this.image = image;
        }

    }

    @Data
    public static class StratigraphyValidationEvent {

        private String status;
        private String name;
        private String icon;
        private String color;


        public StratigraphyValidationEvent(String status, String name, String icon, String color) {
            this.status = status;
            this.name = name;
            this.icon = icon;
            this.color = color;
        }

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
                events.get(2).getRecordingUnitList(),
                events.get(1).getRecordingUnitList(),
                events.get(0).getRecordingUnitList());

    }

    public void handleSelectType() {

        if (fType != null) {
            hasSecondaryTypeOptions = !(this.fetchChildrenOfConcept(fType).isEmpty());
            changeCustomForm();
        } else {
            hasSecondaryTypeOptions = false;
        }

        fSecondaryType = null;
        hasThirdTypeOptions = false;
        fThirdType = null;


    }


    public void handleSelectSecondaryType() {

        if (fSecondaryType != null) {
            hasThirdTypeOptions = !(this.fetchChildrenOfConcept(fSecondaryType).isEmpty());
        } else {
            hasThirdTypeOptions = false;
        }

        fThirdType = null;
    }

    public LocalDate offsetDateTimeToLocalDate(OffsetDateTime offsetDT) {
        return offsetDT.toLocalDate();
    }

    public OffsetDateTime localDateToOffsetDateTime(LocalDate localDate) {
        return localDate.atTime(LocalTime.NOON).atOffset(ZoneOffset.UTC);
    }

    public boolean save() {
        try {

            recordingUnit = save(this.recordingUnit, fType, startDate, endDate);

            // Return page with id
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_INFO,
                            "Info",
                            langBean.msg("recordingunit.created", recordingUnit.getIdentifier())));

            redirectBean.redirectTo("/recording-unit/" + recordingUnit.getId());
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


    public NewRecordingUnitFormBean(RecordingUnitService recordingUnitService,
                                    ActionUnitService actionUnitService, HistoryService historyService,
                                    PersonService personService,
                                    FieldService fieldService, FormService formService,
                                    LangBean langBean,
                                    ConceptService conceptService,
                                    SessionSettingsBean sessionSettingsBean,
                                    FieldConfigurationService fieldConfigurationService,
                                    StratigraphicRelationshipService stratigraphicRelationshipService,
                                    RedirectBean redirectBean) {
        this.recordingUnitService = recordingUnitService;
        this.actionUnitService = actionUnitService;
        this.historyService = historyService;
        this.personService = personService;
        this.fieldService = fieldService;
        this.formService = formService;
        this.langBean = langBean;
        this.conceptService = conceptService;
        this.sessionSettingsBean = sessionSettingsBean;
        this.fieldConfigurationService = fieldConfigurationService;
        this.stratigraphicRelationshipService = stratigraphicRelationshipService;
        this.redirectBean = redirectBean;
    }

    public void reinitializeBean() {
        this.recordingUnit = null;
        this.events = null;
        this.startDate = null;
        this.endDate = null;
        this.isLocalisationFromSIG = false;
        this.concepts = null;
        this.fType = null;
        this.fSecondaryType = null;

    }

    public List<Person> completePerson(String query) {
        if (query == null || query.isEmpty()) {
            return Collections.emptyList();
        }
        query = query.toLowerCase();
        return personService.findAllByNameLastnameContaining(query);
    }

    public void goToNewRecordingUnitPage() {
        redirectBean.redirectTo("/recordingunit/create");
    }

    public void initStratigraphy() {
        events = new ArrayList<>();
        validationEvents = new ArrayList<>();

        // Init neighbors
        Event posterior = new Event("Posterior", "15/10/2020 16:15", "pi pi-arrow-circle-up", "#FF9800");
        try {
            posterior.setRecordingUnitList(stratigraphicRelationshipService.getPosteriorUnits(recordingUnit));
        } catch (RuntimeException e) {
            // add warning
            posterior.setRecordingUnitList(new ArrayList<>());
        }

        posterior.setType(POSTERIOR);

        Event synchronous = new Event("Synchronous", "15/10/2020 14:00", "pi pi-sync", "#673AB7");

        try {
            synchronous.setRecordingUnitList(stratigraphicRelationshipService.getSynchronousUnits(recordingUnit));
        } catch (RuntimeException e) {
            // add warning
            synchronous.setRecordingUnitList(new ArrayList<>());
        }
        synchronous.setType(SYNCHRONOUS);

        Event anterior = new Event("Anterior", "15/10/2020 10:30", "pi pi-arrow-circle-down", "#9C27B0", "game-controller.jpg");

        try {
            anterior.setRecordingUnitList(stratigraphicRelationshipService.getAnteriorUnits(recordingUnit));
        } catch (RuntimeException e) {
            // add warning
            anterior.setRecordingUnitList(new ArrayList<>());
        }
        anterior.setType(ANTERIOR);

        events.add(posterior);
        events.add(synchronous);
        events.add(anterior);

        // Init validation control
        validationEvents.add(new StratigraphyValidationEvent("Inclusions", "Inclusions", "pi pi-check", "green"));
        validationEvents.add(new StratigraphyValidationEvent("Relations certaines", "Relations certaines", "pi pi-spin pi-spinner", "#008dcd"));
        validationEvents.add(new StratigraphyValidationEvent("Relations incertaines", "Relations incertaines", "pi pi-times", "red"));

    }

    public CustomForm getFormForRecordingUnitType(Concept type, Set<ActionUnitFormMapping> availableForms) {
        return availableForms.stream()
                .filter(mapping -> mapping.getPk().getConcept().equals(type) // Vérifier le concept
                        && "RECORDING_UNIT".equals(mapping.getPk().getTableName())) // Vérifier le tableName
                .map(mapping -> mapping.getPk().getForm())
                .findFirst()
                .orElse(null); // Retourner null si aucun match
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
        additionalForm = getFormForRecordingUnitType(fType, formsAvailable);
        if (recordingUnit.getFormResponse() == null) {
            recordingUnit.setFormResponse(new CustomFormResponse());
            recordingUnit.getFormResponse().setAnswers(new HashMap<>());
        }
        recordingUnit.getFormResponse().setForm(additionalForm);
        if (additionalForm != null) {
            initFormResponseAnswers();
        }


    }

    public void initCustomForm() {

        if (recordingUnit.getFormResponse() == null) {
            changeCustomForm();
        } else {
            additionalForm = formService.findById(
                    recordingUnit.getFormResponse().getForm().getId());
            initFormResponseAnswers();
        }

    }

    // Init for new recording units
    public void init(ActionUnit actionUnit) {
        try {
            id = null; // No recording unit requested so we reinit the ID

            log.info("Creating RU");
            reinitializeBean();
            this.recordingUnit = new RecordingUnit();
            this.recordingUnit.setDescription("Nouvelle description");
            this.startDate = offsetDateTimeToLocalDate(now());
            this.recordingUnit.setActionUnit(actionUnitService.findById(actionUnit.getId()));

            // Init size & altimetry
            this.recordingUnit.setSize(new RecordingUnitSize());
            this.recordingUnit.setCreatedByInstitution(actionUnit.getCreatedByInstitution());
            this.recordingUnit.getSize().setSizeUnit("cm");
            this.recordingUnit.setAltitude(new RecordingUnitAltimetry());
            this.recordingUnit.getAltitude().setAltitudeUnit("m");

            this.recordingUnitList = recordingUnitService.findAllByActionUnit(recordingUnit.getActionUnit());
            log.info("here");

            // By default, current user is owner and author
            recordingUnit.setAuthor(sessionSettingsBean.getAuthenticatedUser());
            recordingUnit.setExcavator(sessionSettingsBean.getAuthenticatedUser());

            fType = null;
            fSecondaryType = null;


            initCustomForm();
            initStratigraphy();


        } catch (RuntimeException err) {
            recordingUnitErrorMessage = err.getMessage();
        }
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

    public void initStratiDialog(int type) {

        // should we filter the following list to remove the UE already added with another type/certainty?
        stratiDialogType = type;
        this.recordingUnitList = recordingUnitService.findAllByActionUnit(recordingUnit.getActionUnit());
        stratiDialogSelection = events.get(stratiDialogType).recordingUnitList;
    }

    public void addStratigraphicRelationshipFromSelection() {
        events.get(stratiDialogType).setRecordingUnitList(stratiDialogSelection);
    }

    // Init for existing recording units
    public void init() {
        try {
            if (!FacesContext.getCurrentInstance().isPostback() && this.id != null) {

                log.trace("Loading RU");
                reinitializeBean();
                this.recordingUnit = this.recordingUnitService.findById(this.id);
                if (this.recordingUnit.getStartDate() != null) {
                    this.startDate = offsetDateTimeToLocalDate(this.recordingUnit.getStartDate());
                }
                if (this.recordingUnit.getEndDate() != null) {
                    this.endDate = offsetDateTimeToLocalDate(this.recordingUnit.getEndDate());
                }

                this.isLocalisationFromSIG = false;
                // Init type field
                fType = this.recordingUnit.getType();
                fSecondaryType = this.recordingUnit.getSecondaryType();

                initCustomForm();
                initStratigraphy();

                historyVersion = historyService.findRecordingUnitHistory(recordingUnit);

            } else if (this.id == null) {
                log.error("The Recording Unit page should not be accessed without ID or by direct page path");
                redirectBean.redirectTo(HttpStatus.NOT_FOUND);
            }

        } catch (RecordingUnitNotFoundException e) {
            log.error("Recording unit with ID {} not found", id);
            redirectBean.redirectTo(HttpStatus.NOT_FOUND);
        } catch (RuntimeException err) {
            recordingUnitErrorMessage = "Unable to get recording unit";
            redirectBean.redirectTo(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void visualiseHistory(RecordingUnitHist history) {
        log.trace("History version changed to {}", history.toString());
        revisionToDisplay = history;
    }



    public List<Concept> fetchChildrenOfConcept(Concept concept) {

        UserInfo info = sessionSettingsBean.getUserInfo();

        concepts = conceptService.findDirectSubConceptOf(info, concept);

        return concepts;

    }

    public List<Concept> completeRecordingUnitType(String input) {
        UserInfo info = sessionSettingsBean.getUserInfo();
        try {
            concepts = fieldConfigurationService.fetchAutocomplete(info, RecordingUnit.TYPE_FIELD_CODE, input);
        } catch (NoConfigForFieldException e) {
            log.error(e.getMessage(), e);
        }
        return concepts;
    }

    public List<Concept> completeRecordingUnitSecondaryType(String input) {

        // The main type needs to be set
        if (fType == null) {
            return new ArrayList<>();
        }

        UserInfo info = sessionSettingsBean.getUserInfo();

        return fieldConfigurationService.fetchConceptChildrenAutocomplete(info, fType, input);

    }

    public List<Concept> completeRecordingUnitThirdType(String input) {

        // The main type needs to be set
        if (fSecondaryType == null) {
            return new ArrayList<>();
        }

        UserInfo info = sessionSettingsBean.getUserInfo();

        return fieldConfigurationService.fetchConceptChildrenAutocomplete(info, fSecondaryType, input);

    }

    public String getUrlForRecordingTypeFieldCode() {
        return fieldConfigurationService.getUrlForFieldCode(sessionSettingsBean.getUserInfo(), RecordingUnit.TYPE_FIELD_CODE);
    }

    public String getUrlForRecordingSecondaryType() {
        if (fType != null) {
            return fieldConfigurationService.getUrlOfConcept(fType);
        }
        return null;

    }

    public String getUrlForRecordingThirdType() {
        if (fSecondaryType != null) {
            return fieldConfigurationService.getUrlOfConcept(fSecondaryType);
        }
        return null;
    }


}
