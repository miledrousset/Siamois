package fr.siamois.bean.RecordingUnit;

import fr.siamois.infrastructure.repositories.ark.ArkServerRepository;
import fr.siamois.models.ActionUnit;
import fr.siamois.models.ark.Ark;
import fr.siamois.models.auth.Person;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.models.recordingunit.RecordingUnitAltimetry;
import fr.siamois.models.recordingunit.RecordingUnitSize;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.services.ActionUnitService;
import fr.siamois.services.RecordingUnitService;
import fr.siamois.services.ark.ArkGenerator;
import fr.siamois.services.auth.PersonDetailsService;
import fr.siamois.services.PersonService;
import fr.siamois.services.vocabulary.VocabularyService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
@Component
public class NewRecordingUnitFormBean implements Serializable {


    // Deps
    private final RecordingUnitService recordingUnitService;
    private final ActionUnitService actionUnitService;
    private final PersonService personService;

    // TODO : remove below
    private final ArkServerRepository arkServerRepository;
    private final PersonDetailsService personDetailsService;
    private final VocabularyService vocabularyService;
    // TODO : end to remove

    @Getter
    private RecordingUnit recordingUnit;

    @Getter
    @Setter
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Event> events; // Strati
    private Boolean isLocalisationFromSIG;
    private String recordingUnitErrorMessage;

    @Data
    public static class Event {

        private String status;
        private String date;
        private String icon;
        private String color;
        private String image;

        public Event() {

        }

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

    public String save() {
        try {

            log.debug(String.valueOf(this.recordingUnit));

            // TODO : handle isLocalisationFromSIG and associated fields

            // Generate ark
            // Todo : properly generate ARK
            Ark ark = new Ark();
            ark.setArkServer(arkServerRepository.findArkServerByServerArkUri("http://localhost:8099/siamois").orElse(null));
            ark.setArkId(ArkGenerator.generateArk());

            this.recordingUnit.setArk(ark);

            // handle dates
            if(startDate != null) {this.recordingUnit.setStartDate(localDateToOffsetDateTime(startDate));}
            if(endDate != null) {this.recordingUnit.setEndDate(localDateToOffsetDateTime(endDate));}

            this.recordingUnit = recordingUnitService.save(recordingUnit);
            log.debug("Recording unit saved");
            log.debug(String.valueOf(this.recordingUnit));

        } catch (RuntimeException e) {
            log.error("Error while saving: "+e.getMessage());
            // todo : add error message
            return null;
        }

        // Return page with id
        return "/pages/recordingUnit/recordingUnit.xhtml?faces-redirect=true&id="+this.recordingUnit.getId().toString();
    }

    /**
     * Display a  message on the page.
     * @param severityInfo The severity of the message.
     * @param head  The head of the message.
     * @param detail The message to display.
     */
    private static void displayMessage(FacesMessage.Severity severityInfo, String head, String detail) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(severityInfo, head, detail));
    }


    public NewRecordingUnitFormBean(RecordingUnitService recordingUnitService,
                                    ActionUnitService actionUnitService, PersonService personService, ArkServerRepository arkServerRepository,
                                    PersonDetailsService personDetailsService, VocabularyService vocabularyService) {
        this.recordingUnitService = recordingUnitService;
        this.actionUnitService = actionUnitService;
        this.personService = personService;
        this.arkServerRepository = arkServerRepository;
        this.personDetailsService = personDetailsService;
        this.vocabularyService = vocabularyService;

    }

    public void reinitializeBean() {
        this.recordingUnit = null;
        this.events = null;
        this.startDate = null;
        this.endDate = null;
        this.isLocalisationFromSIG = false;
    }

    public LocalDate offsetDateTimeToLocalDate(OffsetDateTime offsetDT) {
        return offsetDT.toLocalDate();
    }

    public OffsetDateTime localDateToOffsetDateTime(LocalDate localDate) {
        return localDate.atTime(LocalTime.NOON).atOffset(ZoneOffset.UTC);
    }

    public List<Person> completePerson(String query) {
        if (query == null || query.isEmpty()) {
            return Collections.emptyList();
        }
        query = query.toLowerCase();
        return personService.findAllByNameLastnameContaining(query);
    }

    @PostConstruct
    public void init() {
        try {
            if (this.recordingUnit == null) {
                log.info("Creating RU");
                    reinitializeBean();
                    // TODO : clean below, properly get concept
                    Concept c = new Concept();
                    c.setLabel("US");
                    c.setVocabulary(this.vocabularyService.findVocabularyById(14));
                    this.recordingUnit = new RecordingUnit();
                    this.recordingUnit.setType(c);
                    this.recordingUnit.setDescription("Nouvelle description");
                    //this.recordingUnit.setName("Nouvelle unité d'enregistrement");
                    this.startDate = offsetDateTimeToLocalDate(now());
                    // Below is hardcoded but it should not be. TODO
                    ActionUnit actionUnit = this.actionUnitService.findById(4);
                    this.recordingUnit.setActionUnit(actionUnit);

                    // todo : implement real algorithm for serial id
                    this.recordingUnit.setSerial_id(1);
                    this.recordingUnit.setSize(new RecordingUnitSize());
                    this.recordingUnit.getSize().setSize_unit("cm");
                    this.recordingUnit.setAltitude(new RecordingUnitAltimetry());
                    this.recordingUnit.getAltitude().setAltitude_unit("m");

                    events = new ArrayList<>();
                    events.add(new Event("Anterior", "15/10/2020 10:30", "pi pi-arrow-circle-up", "#9C27B0", "game-controller.jpg"));
                    events.add(new Event("Synchronous", "15/10/2020 14:00", "pi pi-sync", "#673AB7"));
                    events.add(new Event("Posterior", "15/10/2020 16:15", "pi pi-arrow-circle-down", "#FF9800"));

            }
        } catch (RuntimeException err) {
            log.error(String.valueOf(err));
        }
    }
}
