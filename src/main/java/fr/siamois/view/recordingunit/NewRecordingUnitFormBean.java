package fr.siamois.view.recordingunit;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.NoConfigForField;
import fr.siamois.domain.models.exceptions.RecordingUnitNotFoundException;
import fr.siamois.domain.models.history.RecordingUnitHist;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.recordingunit.RecordingUnitAltimetry;
import fr.siamois.domain.models.recordingunit.RecordingUnitSize;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.HistoryService;
import fr.siamois.domain.services.PersonService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.domain.services.recordingunit.StratigraphicRelationshipService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.domain.services.vocabulary.FieldService;
import fr.siamois.view.LangBean;
import fr.siamois.view.RedirectBean;
import fr.siamois.view.SessionSettingsBean;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    // View param
    private Long id;  // ID of the requested RU

    // History
    private transient List<RecordingUnitHist> historyVersion;
    private RecordingUnitHist revisionToDisplay = null;

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
    private int stratiDialogType ; // Index from 0 to 2
    private int stratiDialogCertainty ; // 0 or 1
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

    public LocalDate offsetDateTimeToLocalDate(OffsetDateTime offsetDT) {
        return offsetDT.toLocalDate();
    }

    public OffsetDateTime localDateToOffsetDateTime(LocalDate localDate) {
        return localDate.atTime(LocalTime.NOON).atOffset(ZoneOffset.UTC);
    }

    public String save() {
        try {

            save(this.recordingUnit, fType, startDate, endDate);

            // Return page with id
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_INFO,
                            "Info",
                            langBean.msg("recordingunit.created", recordingUnit.getIdentifier())));

            return "/pages/recordingUnit/recordingUnit?faces-redirect=true&id=" + recordingUnit.getId().toString();

        } catch (RuntimeException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            langBean.msg("recordingunit.creationfailed", recordingUnit.getIdentifier()) + ": " + e.getMessage()
                    ));

            log.error("Error while saving: {}", e.getMessage());

        }
        return null;
    }


    public NewRecordingUnitFormBean(RecordingUnitService recordingUnitService,
                                    ActionUnitService actionUnitService, HistoryService historyService,
                                    PersonService personService,
                                    FieldService fieldService,
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

    }

    public List<Person> completePerson(String query) {
        if (query == null || query.isEmpty()) {
            return Collections.emptyList();
        }
        query = query.toLowerCase();
        return personService.findAllByNameLastnameContaining(query);
    }

    public String goToNewRecordingUnitPage() {
        return "/pages/create/recordingUnit.xhtml?faces-redirect=true";
    }

    public void initStratigraphy() {
        events = new ArrayList<>();
        validationEvents = new ArrayList<>();

        // Init neighbors
        Event posterior = new Event("Posterior", "15/10/2020 16:15", "pi pi-arrow-circle-up", "#FF9800");
        posterior.setRecordingUnitList(stratigraphicRelationshipService.getPosteriorUnits(recordingUnit));
        posterior.setType(POSTERIOR);

        Event synchronous = new Event("Synchronous", "15/10/2020 14:00", "pi pi-sync", "#673AB7");
        synchronous.setRecordingUnitList(stratigraphicRelationshipService.getSynchronousUnits(recordingUnit));
        synchronous.setType(SYNCHRONOUS);

        Event anterior = new Event("Anterior", "15/10/2020 10:30", "pi pi-arrow-circle-down", "#9C27B0", "game-controller.jpg");
        anterior.setRecordingUnitList(stratigraphicRelationshipService.getAnteriorUnits(recordingUnit));
        anterior.setType(ANTERIOR);

        events.add(posterior);
        events.add(synchronous);
        events.add(anterior);

        // Init validation control
        validationEvents.add(new StratigraphyValidationEvent("Inclusions", "Inclusions", "pi pi-check", "green"));
        validationEvents.add(new StratigraphyValidationEvent("Relations certaines", "Relations certaines", "pi pi-spin pi-spinner", "#008dcd"));
        validationEvents.add(new StratigraphyValidationEvent("Relations incertaines", "Relations incertaines", "pi pi-times", "red"));

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
            this.recordingUnit.setActionUnit(actionUnit);

            // Init size & altimetry
            this.recordingUnit.setSize(new RecordingUnitSize());
            this.recordingUnit.setCreatedByInstitution(actionUnit.getCreatedByInstitution());
            this.recordingUnit.getSize().setSizeUnit("cm");
            this.recordingUnit.setAltitude(new RecordingUnitAltimetry());
            this.recordingUnit.getAltitude().setAltitudeUnit("m");

            initStratigraphy();


            this.recordingUnitList = recordingUnitService.findAllByActionUnit(recordingUnit.getActionUnit());
            log.info("here");

            // By default, current user is owner and author
            recordingUnit.setAuthor(sessionSettingsBean.getAuthenticatedUser());
            recordingUnit.setExcavator(sessionSettingsBean.getAuthenticatedUser());


        } catch (RuntimeException err) {
            recordingUnitErrorMessage = "Error initializing the form";
        }
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

                initStratigraphy();

                historyVersion = historyService.findRecordingUnitHistory(recordingUnit);
            } else if (this.id == null) {
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

    public List<Concept> completeRecordingUnitType(String input) {
        UserInfo info = sessionSettingsBean.getUserInfo();
        try {
            concepts = fieldConfigurationService.fetchAutocomplete(info, RecordingUnit.TYPE_FIELD_CODE, input);
        } catch (NoConfigForField e) {
            log.error(e.getMessage(), e);
        }
        return concepts;
    }
}
