package fr.siamois.bean.RecordingUnit;

import fr.siamois.bean.LangBean;
import fr.siamois.bean.RecordingUnit.utils.RecordingUnitUtils;
import fr.siamois.infrastructure.api.dto.ConceptFieldDTO;
import fr.siamois.models.ActionUnit;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.NoConfigForField;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.models.recordingunit.RecordingUnitAltimetry;
import fr.siamois.models.recordingunit.RecordingUnitSize;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.models.vocabulary.FieldConfigurationWrapper;
import fr.siamois.models.vocabulary.Vocabulary;
import fr.siamois.services.ActionUnitService;
import fr.siamois.services.PersonService;
import fr.siamois.services.RecordingUnitService;
import fr.siamois.services.vocabulary.FieldConfigurationService;
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
import java.util.stream.Collectors;


import static java.time.OffsetDateTime.now;

@Data
@Slf4j
@SessionScoped
@Component
public class NewRecordingUnitFormBean implements Serializable  {

    // Deps
    private final RecordingUnitService recordingUnitService;
    private final ActionUnitService actionUnitService;
    private final PersonService personService;
    private final RecordingUnitUtils recordingUnitUtils;
    private final FieldService fieldService;
    private final FieldConfigurationService fieldConfigurationService;
    private final LangBean langBean;


    // Local
    private RecordingUnit recordingUnit;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Event> events; // Strati
    private Boolean isLocalisationFromSIG;
    private String recordingUnitErrorMessage;
    private List<ConceptFieldDTO> concepts;
    private FieldConfigurationWrapper configurationWrapper;
    private Concept selectedConcept = null;
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

    public List<Person> completePerson(String query) {
        return recordingUnitUtils.completePerson(query);
    }

    public String save() {
        try {

            Vocabulary vocabulary = configurationWrapper.vocabularyConfig();
            if (vocabulary == null) vocabulary = configurationWrapper.vocabularyCollectionsConfig().get(0).getVocabulary();

            this.recordingUnit = recordingUnitUtils.save(recordingUnit, vocabulary, fType, startDate, endDate);
            log.error("Recording unit saved");
            log.error(String.valueOf(this.recordingUnit));

            // Return page with id
            return "/pages/recordingUnit/recordingUnit?faces-redirect=true&id="+this.recordingUnit.getId().toString();

        } catch (RuntimeException e) {
            log.error("Error while saving: "+e.getMessage());
            // todo : add error message
            return null;
        }

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
                                    ActionUnitService actionUnitService,
                                    PersonService personService,
                                    RecordingUnitUtils recordingUnitUtils,
                                    FieldService fieldService,
                                    FieldConfigurationService fieldConfigurationService,
                                    LangBean langBean
                                    ) {
        this.recordingUnitService = recordingUnitService;
        this.actionUnitService = actionUnitService;
        this.personService = personService;
        this.recordingUnitUtils = recordingUnitUtils;
        this.fieldService = fieldService;
        this.fieldConfigurationService = fieldConfigurationService;
        this.langBean = langBean;
    }

    public void reinitializeBean() {
        this.recordingUnit = null;
        this.events = null;
        this.startDate = null;
        this.endDate = null;
        this.isLocalisationFromSIG = false;
        this.concepts = null;
        this.selectedConcept = null;
        this.fType = null;
    }

    /**
     * Fetch the autocomplete results on API for the type field and add them to the list of concepts.
     * @param input the input of the user
     * @return the list of concepts that match the input to display in the autocomplete
     */
    public List<ConceptFieldDTO> completeRecordingUnitType(String input) {
        Person person = AuthenticatedUserUtils.getAuthenticatedUser().orElseThrow(() -> new IllegalStateException("User should be connected"));

        try {
            if (configurationWrapper == null) {
                configurationWrapper = fieldConfigurationService.fetchConfigurationOfFieldCode(person, RecordingUnit.TYPE_FIELD_CODE);
            }

            concepts = fieldService.fetchAutocomplete(configurationWrapper, input, langBean.getLanguageCode());
            return concepts;

        } catch (NoConfigForField e) {
            log.error("No collection for field " + RecordingUnit.TYPE_FIELD_CODE);
            return new ArrayList<>();
        }
    }


    public void init() {
        try {
            if (this.recordingUnit == null) {
                log.info("Creating RU");
                    reinitializeBean();
                    this.recordingUnit = new RecordingUnit();
                    this.recordingUnit.setDescription("Nouvelle description");
                    this.startDate = recordingUnitUtils.offsetDateTimeToLocalDate(now());
                    // Below is hardcoded but it should not be. TODO
                    ActionUnit actionUnit = this.actionUnitService.findById(4);
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
            }
        } catch (RuntimeException err) {
            log.error(String.valueOf(err));
        }
    }
}
