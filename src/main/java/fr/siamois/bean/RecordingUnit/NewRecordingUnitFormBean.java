package fr.siamois.bean.RecordingUnit;

import fr.siamois.bean.LangBean;
import fr.siamois.bean.RecordingUnit.utils.RecordingUnitUtils;
import fr.siamois.infrastructure.api.dto.ConceptFieldDTO;
import fr.siamois.models.actionunit.ActionUnit;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.NoConfigForField;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.models.recordingunit.RecordingUnitAltimetry;
import fr.siamois.models.recordingunit.RecordingUnitSize;
import fr.siamois.models.vocabulary.FieldConfigurationWrapper;
import fr.siamois.services.ActionUnitService;
import fr.siamois.services.PersonService;
import fr.siamois.services.RecordingUnitService;
import fr.siamois.services.vocabulary.FieldService;
import fr.siamois.utils.AuthenticatedUserUtils;
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

    // Local
    private RecordingUnit recordingUnit;
    private String recordingUnitErrorMessage;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Event> events; // Strati
    private Boolean isLocalisationFromSIG;
    private List<RecordingUnit> recordingUnitList;
    private List<RecordingUnit> stratigraphySelectedRecordingUnit;

    private List<ConceptFieldDTO> concepts;
    private ConceptFieldDTO fType = null;
    private FieldConfigurationWrapper configurationWrapper;

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


    public List<Person> completePerson(String query) {
        return recordingUnitUtils.completePerson(query);
    }

    public String save() {
        try {

            this.recordingUnit = recordingUnitUtils.save(recordingUnit, configurationWrapper, fType, startDate, endDate);
            // Return page with id
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_INFO,
                            "Info",
                            langBean.msg("recordingunit.created", this.recordingUnit.getSerial_id())));

            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

            return "/pages/recordingUnit/recordingUnit?faces-redirect=true&id=" + this.recordingUnit.getId().toString();

        } catch (RuntimeException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            langBean.msg("recordingunit.creationfailed", this.recordingUnit.getSerial_id())));

            log.error("Error while saving: " + e.getMessage());
            // todo : add error message
            return null;
        }

    }

    /**
     * Display a  message on the page.
     *
     * @param severityInfo The severity of the message.
     * @param head         The head of the message.
     * @param detail       The message to display.
     */
    private static void displayMessage(FacesMessage.Severity severityInfo, String head, String detail) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(severityInfo, head, detail));
    }


    public NewRecordingUnitFormBean(RecordingUnitService recordingUnitService,
                                    ActionUnitService actionUnitService,
                                    PersonService personService,
                                    RecordingUnitUtils recordingUnitUtils, FieldService fieldService, LangBean langBean
    ) {
        this.recordingUnitService = recordingUnitService;
        this.actionUnitService = actionUnitService;
        this.personService = personService;
        this.recordingUnitUtils = recordingUnitUtils;
        this.fieldService = fieldService;
        this.langBean = langBean;
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

    /**
     * Fetch the autocomplete results on API for the type field and add them to the list of concepts.
     *
     * @param input the input of the user
     * @return the list of concepts that match the input to display in the autocomplete
     */
    public List<ConceptFieldDTO> completeRecordingUnitType(String input) {

        Person person = AuthenticatedUserUtils.getAuthenticatedUser().orElseThrow(() -> new IllegalStateException("User should be connected"));

        try {
            if(this.configurationWrapper == null) {
                this.configurationWrapper = fieldConfigurationService.fetchConfigurationOfFieldCode(person, RecordingUnit.TYPE_FIELD_CODE);
            }
        } catch (NoConfigForField e) {
            log.error("No collection for field " + RecordingUnit.TYPE_FIELD_CODE);
        }

        concepts = fieldService.fetchAutocomplete(configurationWrapper, input, langBean.getLanguageCode());
        return concepts;
    }


    public String goToNewRecordingUnitPage() {
        return "/pages/create/recordingUnit.xhtml?faces-redirect=true";
    }

    public void fetchAllRecordingUnitsInSameActionUnit() {
        this.recordingUnitList = recordingUnitService.findAllByActionUnit(recordingUnit.getActionUnit());
    }

    public void addStratigraphicRelationshipFromSelection() {

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
                this.recordingUnit.setSerial_id(1);
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
}
