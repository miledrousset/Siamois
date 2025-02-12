package fr.siamois.bean.recordingunit;

import fr.siamois.bean.LangBean;
import fr.siamois.bean.SessionSettings;
import fr.siamois.models.UserInfo;
import fr.siamois.models.actionunit.ActionUnit;
import fr.siamois.models.auth.Person;

import fr.siamois.models.exceptions.NoConfigForField;
import fr.siamois.models.history.RecordingUnitHist;
import fr.siamois.models.history.SpatialUnitHist;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.models.recordingunit.RecordingUnitAltimetry;
import fr.siamois.models.recordingunit.RecordingUnitSize;
import fr.siamois.services.HistoryService;
import fr.siamois.services.actionunit.ActionUnitService;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.services.PersonService;
import fr.siamois.services.RecordingUnitService;
import fr.siamois.services.actionunit.ActionUnitService;
import fr.siamois.services.vocabulary.ConceptService;
import fr.siamois.services.vocabulary.FieldConfigurationService;
import fr.siamois.services.vocabulary.FieldService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
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
    private final SessionSettings sessionSettings;
    private final transient FieldConfigurationService fieldConfigurationService;

    // Local
    private RecordingUnit recordingUnit;
    private String recordingUnitErrorMessage;
    private LocalDate startDate;
    private LocalDate endDate;
    private transient List<Event> events; // Strati
    private Boolean isLocalisationFromSIG;
    private transient List<RecordingUnit> recordingUnitList;
    private transient List<RecordingUnit> stratigraphySelectedRecordingUnit;

    private transient List<Concept> concepts;
    private Concept fType = null;

    // View param
    private Long id;  // ID of the requested RU

    // History
    private transient List<RecordingUnitHist> historyVersion;
    private RecordingUnitHist revisionToDisplay = null;

    @Data
    public static class Event {

        private String status;
        private String date;
        private String icon;
        private String color;
        private String image;

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


    public RecordingUnit save(RecordingUnit recordingUnit,
                              Concept typeConcept,
                              LocalDate startDate,
                              LocalDate endDate) {

        // TODO : handle isLocalisationFromSIG and associated fields

        // handle dates
        if (startDate != null) {
            recordingUnit.setStartDate(localDateToOffsetDateTime(startDate));
        }
        if (endDate != null) {
            recordingUnit.setEndDate(localDateToOffsetDateTime(endDate));
        }

        return recordingUnitService.save(recordingUnit, typeConcept);

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

            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

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
                                    SessionSettings sessionSettings, FieldConfigurationService fieldConfigurationService) {
        this.recordingUnitService = recordingUnitService;
        this.actionUnitService = actionUnitService;
        this.historyService = historyService;
        this.personService = personService;
        this.fieldService = fieldService;
        this.langBean = langBean;
        this.conceptService = conceptService;
        this.sessionSettings = sessionSettings;
        this.fieldConfigurationService = fieldConfigurationService;

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

            // Init strati. TODO : real implementation
            events = new ArrayList<>();
            events.add(new Event("Anterior", "15/10/2020 10:30", "pi pi-arrow-circle-up", "#9C27B0", "game-controller.jpg"));
            events.add(new Event("Synchronous", "15/10/2020 14:00", "pi pi-sync", "#673AB7"));
            events.add(new Event("Posterior", "15/10/2020 16:15", "pi pi-arrow-circle-down", "#FF9800"));
            this.recordingUnitList = recordingUnitService.findAllByActionUnit(recordingUnit.getActionUnit());
            log.info("here");

            // By default, current user is owner and author
            recordingUnit.setAuthor(sessionSettings.getAuthenticatedUser());
            recordingUnit.setExcavator(sessionSettings.getAuthenticatedUser());


        } catch (RuntimeException err) {
            recordingUnitErrorMessage = "Error initializing the form";
        }
    }

    // Init for existing recording units
    public void init() {
        try {
            if (!FacesContext.getCurrentInstance().isPostback()) {
                if (this.id != null) {

                    log.info("Loading RU");
                    reinitializeBean();
                    this.recordingUnit = this.recordingUnitService.findById(this.id);
                    if (this.recordingUnit.getStartDate() != null) {
                        this.startDate = offsetDateTimeToLocalDate(this.recordingUnit.getStartDate());
                    }
                    if (this.recordingUnit.getEndDate() != null) {
                        this.endDate = offsetDateTimeToLocalDate(this.recordingUnit.getEndDate());
                    }
                    // TODO handle isLocalisationFromSIG properly
                    this.isLocalisationFromSIG = false;
                    // Init type field
                    fType = this.recordingUnit.getType();

                    historyVersion = historyService.findRecordingUnitHistory(recordingUnit);

                }
            }

        } catch (RuntimeException err) {
            recordingUnitErrorMessage = "Unable to get recording unit";
        }
    }

    public void visualiseHistory(RecordingUnitHist history) {
        log.trace("History version changed to {}", history.toString());
        revisionToDisplay = history;
    }

    public List<Concept> completeRecordingUnitType(String input) {
        UserInfo info = sessionSettings.getUserInfo();
        try {
            concepts = fieldConfigurationService.fetchAutocomplete(info, RecordingUnit.TYPE_FIELD_CODE, input);
        } catch (NoConfigForField e) {
            log.error(e.getMessage(), e);
        }
        return concepts;
    }
}
