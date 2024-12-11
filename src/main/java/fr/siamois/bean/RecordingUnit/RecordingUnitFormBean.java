package fr.siamois.bean.RecordingUnit;

import fr.siamois.bean.RecordingUnit.utils.RecordingUnitUtilsBean;
import fr.siamois.infrastructure.repositories.ark.ArkServerRepository;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.services.ActionUnitService;
import fr.siamois.services.RecordingUnitService;
import fr.siamois.services.auth.PersonDetailsService;
import fr.siamois.services.auth.PersonService;
import fr.siamois.services.vocabulary.VocabularyService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import static java.time.OffsetDateTime.now;

@Data
@Slf4j
@SessionScoped
@Component
public class RecordingUnitFormBean implements Serializable {


    // Deps
    private final RecordingUnitService recordingUnitService;
    private final ActionUnitService actionUnitService;
    private final PersonService personService;
    private final RecordingUnitUtilsBean recordingUnitUtilsBean;

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

            this.recordingUnit = recordingUnitUtilsBean.save(recordingUnit, startDate, endDate);
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
                                 ActionUnitService actionUnitService, PersonService personService, RecordingUnitUtilsBean recordingUnitUtilsBean, ArkServerRepository arkServerRepository,
                                 PersonDetailsService personDetailsService, VocabularyService vocabularyService) {
        this.recordingUnitService = recordingUnitService;
        this.actionUnitService = actionUnitService;
        this.personService = personService;
        this.recordingUnitUtilsBean = recordingUnitUtilsBean;
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

    @PostConstruct
    public void init() {
        log.error("id"+String.valueOf(this.id));
        try {
           if(this.id != null) {
                log.info("Loading RU");
                reinitializeBean();
                this.recordingUnit = this.recordingUnitService.findById(this.id);
                if(this.recordingUnit.getStartDate() != null) {this.startDate = recordingUnitUtilsBean.offsetDateTimeToLocalDate(this.recordingUnit.getStartDate());}
                if(this.recordingUnit.getEndDate()!=null) {this.endDate = recordingUnitUtilsBean.offsetDateTimeToLocalDate(this.recordingUnit.getEndDate());}
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
