package fr.siamois.bean.recordingunit;

import fr.siamois.bean.LangBean;
import fr.siamois.bean.recordingunit.utils.RecordingUnitUtils;
import fr.siamois.bean.SessionSettings;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.services.actionunit.ActionUnitService;
import fr.siamois.services.PersonService;
import fr.siamois.services.RecordingUnitService;
import fr.siamois.services.vocabulary.ConceptService;
import fr.siamois.services.vocabulary.FieldService;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;


@Data
@Slf4j
@SessionScoped
@Component
public class RecordingUnitFormBean implements Serializable {

    // Deps
    private final transient RecordingUnitService recordingUnitService;
    private final transient ActionUnitService actionUnitService;
    private final transient PersonService personService;
    private final transient RecordingUnitUtils recordingUnitUtils;
    private final transient FieldService fieldService;
    private final LangBean langBean;
    private final transient ConceptService conceptService;
    private final transient SessionSettings sessionSettings;

    private RecordingUnit recordingUnit;
    private String recordingUnitErrorMessage; // If error while initing the recording unit
    private Long id;  // ID of the requested RU
    private LocalDate startDate;
    private LocalDate endDate;
    private transient List<Event> events; // Strati
    private Boolean isLocalisationFromSIG;
    private transient List<Concept> concepts;
    private Concept fType = null;

    @Data
    public static class Event {
        private String status;
        private String date;
        private String icon;
        private String color;
        private String image;
    }

    public String save() {
        this.recordingUnit = recordingUnitUtils.save(recordingUnit, fType, startDate, endDate);
        return recordingUnitUtils.save(recordingUnit, langBean);
    }

    public RecordingUnitFormBean(
            RecordingUnitService recordingUnitService,
            ActionUnitService actionUnitService,
            PersonService personService,
            RecordingUnitUtils recordingUnitUtils, FieldService fieldService, LangBean langBean,
            ConceptService conceptService, SessionSettings sessionSettings) {
        this.recordingUnitService = recordingUnitService;
        this.actionUnitService = actionUnitService;
        this.personService = personService;
        this.recordingUnitUtils = recordingUnitUtils;

        this.fieldService = fieldService;
        this.langBean = langBean;
        this.conceptService = conceptService;
        this.sessionSettings = sessionSettings;
    }

    public void reinitializeBean() {
        this.recordingUnit = null;
        this.events = null;
        this.startDate = null;
        this.endDate = null;
        this.isLocalisationFromSIG = false;
        this.concepts = null;
        this.fType = null;
        recordingUnitErrorMessage = null;
    }

    @PostConstruct
    public void init() {

        try {
            if (this.id != null) {
                log.info("Loading RU");
                reinitializeBean();
                this.recordingUnit = this.recordingUnitService.findById(this.id);
                if (this.recordingUnit.getStartDate() != null) {
                    this.startDate = recordingUnitUtils.offsetDateTimeToLocalDate(this.recordingUnit.getStartDate());
                }
                if (this.recordingUnit.getEndDate() != null) {
                    this.endDate = recordingUnitUtils.offsetDateTimeToLocalDate(this.recordingUnit.getEndDate());
                }
                // TODO handle isLocalisationFromSIG properly
                this.isLocalisationFromSIG = false;
                // Init type field
                fType = this.recordingUnit.getType();

            } else {
                recordingUnitErrorMessage = "Invalid recording unit ID";
            }
        } catch (RuntimeException err) {
            recordingUnitErrorMessage = "Unable to get recording unit";
        }
    }
}
