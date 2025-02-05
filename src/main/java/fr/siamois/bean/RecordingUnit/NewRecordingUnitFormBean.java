package fr.siamois.bean.RecordingUnit;

import fr.siamois.bean.LangBean;
import fr.siamois.bean.RecordingUnit.utils.RecordingUnitUtils;
import fr.siamois.bean.SessionSettings;
import fr.siamois.models.actionunit.ActionUnit;
import fr.siamois.models.exceptions.NoConfigForField;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.models.recordingunit.RecordingUnitAltimetry;
import fr.siamois.models.recordingunit.RecordingUnitSize;
import fr.siamois.services.actionunit.ActionUnitService;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.services.PersonService;
import fr.siamois.services.RecordingUnitService;
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
import java.util.ArrayList;
import java.util.List;

import static java.time.OffsetDateTime.now;

@Data
@Slf4j
@SessionScoped
@Component
public class NewRecordingUnitFormBean implements Serializable {

    // Deps
    private final RecordingUnitService recordingUnitService;
    private final ActionUnitService actionUnitService;
    private final PersonService personService;
    private final RecordingUnitUtils recordingUnitUtils;
    private final FieldService fieldService;
    private final LangBean langBean;
    private final ConceptService conceptService;
    private final SessionSettings sessionSettings;
    private final FieldConfigurationService fieldConfigurationService;

    // Local
    private RecordingUnit recordingUnit;
    private String recordingUnitErrorMessage;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Event> events; // Strati
    private Boolean isLocalisationFromSIG;
    private List<RecordingUnit> recordingUnitList;
    private List<RecordingUnit> stratigraphySelectedRecordingUnit;

    private List<Concept> concepts;
    private Concept fType = null;

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

    public String save() {
        try {

            this.recordingUnit = recordingUnitUtils.save(recordingUnit, fType, startDate, endDate);
            // Return page with id
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_INFO,
                            "Info",
                            langBean.msg("recordingunit.created", this.recordingUnit.getCode())));

            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

            return "/pages/recordingUnit/recordingUnit?faces-redirect=true&id=" + this.recordingUnit.getId().toString();

        } catch (RuntimeException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            langBean.msg("recordingunit.creationfailed", this.recordingUnit.getCode())));

            log.error("Error while saving: " + e.getMessage());
            // todo : add error message
            return null;
        }

    }


    public NewRecordingUnitFormBean(RecordingUnitService recordingUnitService,
                                    ActionUnitService actionUnitService,
                                    PersonService personService,
                                    RecordingUnitUtils recordingUnitUtils,
                                    FieldService fieldService,
                                    LangBean langBean,
                                    ConceptService conceptService,
                                    SessionSettings sessionSettings, FieldConfigurationService fieldConfigurationService) {
        this.recordingUnitService = recordingUnitService;
        this.actionUnitService = actionUnitService;
        this.personService = personService;
        this.recordingUnitUtils = recordingUnitUtils;
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

    public String goToNewRecordingUnitPage() {
        return "/pages/create/recordingUnit.xhtml?faces-redirect=true";
    }

    public void init(ActionUnit actionUnit) {
        try {
            if (this.recordingUnit == null) {
                log.info("Creating RU");
                reinitializeBean();
                this.recordingUnit = new RecordingUnit();
                this.recordingUnit.setDescription("Nouvelle description");
                this.startDate = recordingUnitUtils.offsetDateTimeToLocalDate(now());
                this.recordingUnit.setActionUnit(actionUnit);
                // todo : implement real algorithm for serial id
                this.recordingUnit.setCode(1);
                // Init size & altimetry
                this.recordingUnit.setSize(new RecordingUnitSize());
                this.recordingUnit.getSize().setSize_unit("cm");
                this.recordingUnit.setAltitude(new RecordingUnitAltimetry());
                this.recordingUnit.getAltitude().setAltitude_unit("m");
                // Init strati. TODO : real implementation
                events = new ArrayList<>();
                events.add(new Event("Anterior", "15/10/2020 10:30", "pi pi-arrow-circle-up", "#9C27B0", "game-controller.jpg"));
                events.add(new Event("Synchronous", "15/10/2020 14:00", "pi pi-sync", "#673AB7"));
                events.add(new Event("Posterior", "15/10/2020 16:15", "pi pi-arrow-circle-down", "#FF9800"));
                this.recordingUnitList = recordingUnitService.findAllByActionUnit(recordingUnit.getActionUnit());
                log.info("here");

            }
        } catch (RuntimeException err) {
            recordingUnitErrorMessage = "Error initializing the form";
        }
    }


    public List<Concept> completeRecordingUnitType(String input) {
        log.trace("completeRecordingUnitType called");
        try {
            return fieldConfigurationService.fetchAutocomplete(sessionSettings.getUserInfo(), RecordingUnit.TYPE_FIELD_CODE, input);
        } catch (NoConfigForField e) {
            log.error(e.getMessage(), e);
            return new ArrayList<>();
        }
    }
}
