package fr.siamois.bean.Field;

import fr.siamois.bean.LangBean;
import fr.siamois.bean.SessionSettings;
import fr.siamois.infrastructure.api.dto.ConceptFieldDTO;
import fr.siamois.models.SpatialUnit;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.NoConfigForField;
import fr.siamois.models.exceptions.SpatialUnitAlreadyExistsException;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.models.vocabulary.FieldConfigurationWrapper;
import fr.siamois.models.vocabulary.Vocabulary;
import fr.siamois.services.vocabulary.FieldConfigurationService;
import fr.siamois.services.vocabulary.FieldService;
import fr.siamois.utils.MessageUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>This bean handles the creation of new Spatial Unit</p>
 *
 * @author Julien Linget
 */
@Getter
@Setter
@Slf4j
@Component
@SessionScoped
public class SpatialUnitFieldBean implements Serializable {

    // Injections
    private final FieldService fieldService;
    private final FieldConfigurationService fieldConfigurationService;
    private final LangBean langBean;
    private final SessionSettings sessionSettings;

    // Storage
    private List<SpatialUnit> refSpatialUnits = new ArrayList<>();
    private List<String> labels;
    private List<ConceptFieldDTO> concepts;
    private FieldConfigurationWrapper configurationWrapper;

    // Fields
    private Concept selectedConcept = null;
    private String fName = "";
    private String fCategory = "";
    private List<SpatialUnit> fParentsSpatialUnits = new ArrayList<>();

    public SpatialUnitFieldBean(FieldService fieldService, FieldConfigurationService fieldConfigurationService, LangBean langBean, SessionSettings sessionSettings) {
        this.fieldService = fieldService;
        this.fieldConfigurationService = fieldConfigurationService;
        this.langBean = langBean;
        this.sessionSettings = sessionSettings;
    }

    /**
     * Called on page rendering.
     * Reset all fields.
     */
    public void init() {
        refSpatialUnits = fieldService.fetchAllSpatialUnits();
        labels = refSpatialUnits.stream()
                .map(SpatialUnit::getName)
                .collect(Collectors.toList());
        concepts = null;
        selectedConcept = null;
        fName = "";
        fCategory = "";
        fParentsSpatialUnits = new ArrayList<>();
    }

    /**
     * Save the spatial unit in the database.
     * Display a message if the spatial unit already exists.
     * Display a message if the spatial unit has been created.
     * @throws IllegalStateException if the collections are not defined
     */
    public void save() {
        ConceptFieldDTO selectedConceptFieldDTO = getSelectedConceptFieldDTO().orElseThrow(() -> new IllegalStateException("No concept selected"));

        Vocabulary vocabulary = configurationWrapper.vocabularyConfig();
        if (vocabulary == null) vocabulary = configurationWrapper.vocabularyCollectionsConfig().get(0).getVocabulary();

        try {
            SpatialUnit saved = fieldService.saveSpatialUnit(fName, vocabulary, selectedConceptFieldDTO, fParentsSpatialUnits);

            MessageUtils.displayInfoMessage(langBean, "spatialunit.created", saved.getName());
        } catch (SpatialUnitAlreadyExistsException e) {
            log.error(e.getMessage(), e);
            MessageUtils.displayErrorMessage(langBean, "commons.error.spatialunit.alreadyexist", fName);
        }
    }

    /**
     * Fetch the autocomplete results on API for the category field and add them to the list of concepts.
     * @param input the input of the user
     * @return the list of concepts that match the input to display in the autocomplete
     */
    public List<String> completeCategory(String input) {
        Person person = sessionSettings.getAuthenticatedUser();

        try {
            if (configurationWrapper == null) {
                configurationWrapper = fieldConfigurationService.fetchConfigurationOfFieldCode(person, SpatialUnit.CATEGORY_FIELD_CODE);
            }

            concepts = fieldService.fetchAutocomplete(configurationWrapper, input, langBean.getLanguageCode());
            return concepts.stream()
                    .map(ConceptFieldDTO::getLabel)
                    .collect(Collectors.toList());

        } catch (NoConfigForField e) {
            log.error("No collection for field " + SpatialUnit.CATEGORY_FIELD_CODE);
            return new ArrayList<>();
        }
    }

    /**
     * Find the concept selected by the user.
     * @return the concept selected by the user
     */
    private Optional<ConceptFieldDTO> getSelectedConceptFieldDTO() {
        return concepts.stream()
                .filter(conceptFieldDTO -> conceptFieldDTO.getLabel().equalsIgnoreCase(fCategory))
                .findFirst();
    }

}
