package fr.siamois.bean.RecordingUnit;

import fr.siamois.infrastructure.repositories.ark.ArkServerRepository;
import fr.siamois.models.ark.Ark;
import fr.siamois.models.auth.Person;
import fr.siamois.models.recordingunit.RecordingUnit;
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
import java.util.Collections;
import java.util.List;

@Data
@Slf4j
@Component
public class RecordingUnitFormBean implements Serializable {


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
    private Long id;  // ID of the requested RU
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
        return "/pages/recordingUnit/recordingUnit.xhtml?id="+this.recordingUnit.getId().toString();
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


    public RecordingUnitFormBean(RecordingUnitService recordingUnitService,
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
        log.error("id"+String.valueOf(this.id));
        try {
           if(this.id != null) {
                log.info("Loading RU");
                reinitializeBean();
                this.recordingUnit = this.recordingUnitService.findById(this.id);
                if(this.recordingUnit.getStartDate() != null) {this.startDate = offsetDateTimeToLocalDate(this.recordingUnit.getStartDate());}
                if(this.recordingUnit.getEndDate()!=null) {this.endDate = offsetDateTimeToLocalDate(this.recordingUnit.getEndDate());}
                // TODO handle isLocalisationFromSIG properly
                this.isLocalisationFromSIG = false;
            }
           else {
               // todo: handle error
           }
        } catch (RuntimeException err) {
            log.error(String.valueOf(err));
        }
    }
}
