package fr.siamois.bean.RecordingUnit;

import fr.siamois.bean.LangBean;
import fr.siamois.bean.RecordingUnit.utils.RecordingUnitUtils;
import fr.siamois.infrastructure.api.dto.ConceptFieldDTO;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.NoConfigForField;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.models.vocabulary.FieldConfigurationWrapper;
import fr.siamois.services.ActionUnitService;
import fr.siamois.services.PersonService;
import fr.siamois.services.RecordingUnitService;
import fr.siamois.services.vocabulary.FieldConfigurationService;
import fr.siamois.services.vocabulary.FieldService;
import fr.siamois.utils.AuthenticatedUserUtils;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
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
    private final RecordingUnitService recordingUnitService;
    private final ActionUnitService actionUnitService;
    private final PersonService personService;
    private final RecordingUnitUtils recordingUnitUtils;
    private final FieldConfigurationService fieldConfigurationService;
    private final FieldService fieldService;
    private final LangBean langBean;

    private RecordingUnit recordingUnit;
    private String recordingUnitErrorMessage; // If error while initing the recording unit
    private Long id;  // ID of the requested RU
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Event> events; // Strati
    private Boolean isLocalisationFromSIG;
    private List<ConceptFieldDTO> concepts;
    private FieldConfigurationWrapper configurationWrapper;
    private ConceptFieldDTO fType = null;

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

            this.recordingUnit = recordingUnitUtils.save(recordingUnit, configurationWrapper, fType, startDate, endDate);

            // Return page with id
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_INFO,
                            "Info",
                            langBean.msg("recordingunit.updated", this.recordingUnit.getSerial_id())));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            return "/pages/recordingUnit/recordingUnit?faces-redirect=true&id=" + this.recordingUnit.getId().toString();

        } catch (RuntimeException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            langBean.msg("recordingunit.updatefailed", this.recordingUnit.getSerial_id())));

            log.error("Error while saving: " + e.getMessage());
            // todo : add error message
            return null;
        }

        // Return page with id

    }

    public List<Person> completePerson(String query) {
        return recordingUnitUtils.completePerson(query);
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


    public RecordingUnitFormBean(
            RecordingUnitService recordingUnitService,
            ActionUnitService actionUnitService,
            PersonService personService,
            RecordingUnitUtils recordingUnitUtils, FieldConfigurationService fieldConfigurationService, FieldService fieldService, LangBean langBean
    ) {
        this.recordingUnitService = recordingUnitService;
        this.actionUnitService = actionUnitService;
        this.personService = personService;
        this.recordingUnitUtils = recordingUnitUtils;

        this.fieldConfigurationService = fieldConfigurationService;
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
        recordingUnitErrorMessage = null;
        Person person = AuthenticatedUserUtils.getAuthenticatedUser().orElseThrow(() -> new IllegalStateException("User should be connected"));

        try {
            this.configurationWrapper = fieldConfigurationService.fetchConfigurationOfFieldCode(person, RecordingUnit.TYPE_FIELD_CODE);
        } catch (NoConfigForField e) {
            log.error("No collection for field " + RecordingUnit.TYPE_FIELD_CODE);
        }
    }

    /**
     * Fetch the autocomplete results on API for the type field and add them to the list of concepts.
     *
     * @param input the input of the user
     * @return the list of concepts that match the input to display in the autocomplete
     */
    public List<ConceptFieldDTO> completeRecordingUnitType(String input) {

        concepts = fieldService.fetchAutocomplete(configurationWrapper, input, langBean.getLanguageCode());
        return concepts;
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
                Concept typeConcept = this.recordingUnit.getType();
                fType = new ConceptFieldDTO();
                fType.setLabel(typeConcept.getLabel());
                // If thesaurus we can reconstruct the DTO
                fType.setUri(typeConcept.getVocabulary().getBaseUri()+"?idc="+typeConcept.getExternalId()+"&idt="+typeConcept.getVocabulary().getExternalVocabularyId());

            } else {
                recordingUnitErrorMessage = "Invalid recording unit ID";
            }
        } catch (RuntimeException err) {
            recordingUnitErrorMessage = "Unable to get recording unit";
        }
    }
}
