package fr.siamois.bean.Field;

import fr.siamois.infrastructure.api.dto.ConceptFieldDTO;
import fr.siamois.models.SpatialUnit;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.NoConfigForField;
import fr.siamois.models.exceptions.SpatialUnitAlreadyExistsException;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.models.vocabulary.Vocabulary;
import fr.siamois.models.vocabulary.VocabularyCollection;
import fr.siamois.services.FieldConfigurationService;
import fr.siamois.services.FieldService;
import fr.siamois.utils.AuthenticatedUserUtils;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
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

    // Storage
    private List<SpatialUnit> refSpatialUnits = new ArrayList<>();
    private List<String> labels;
    private List<ConceptFieldDTO> concepts;
    private String langcode = "fr";
    private List<VocabularyCollection> collections = null;
    private Vocabulary vocabulary = null;

    // Fields
    private Concept selectedConcept = null;
    private String fName = "";
    private String fCategory = "";
    private List<SpatialUnit> fParentsSpatialUnits = new ArrayList<>();

    public SpatialUnitFieldBean(FieldService fieldService, FieldConfigurationService fieldConfigurationService) {
        this.fieldService = fieldService;
        this.fieldConfigurationService = fieldConfigurationService;
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

        if (collections == null) throw new IllegalStateException("Collections should be defined before saving the spatial unit");

        if (vocabulary == null) {
            vocabulary = collections.get(0).getVocabulary();
        }

        try {
            SpatialUnit saved = fieldService.saveSpatialUnit(fName, vocabulary, selectedConceptFieldDTO, fParentsSpatialUnits);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Info", "L'unité spatiale " + saved.getName() + " a été crée."));
        } catch (SpatialUnitAlreadyExistsException e) {
            log.error(e.getMessage(), e);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error", "L'unité spatiale existe déjà"));
        }
    }

    /**
     * Fetch the autocomplete results on API for the category field and add them to the list of concepts.
     * @param input the input of the user
     * @return the list of concepts that match the input to display in the autocomplete
     */
    public List<String> completeCategory(String input) {
        Person person = AuthenticatedUserUtils.getAuthenticatedUser().orElseThrow(() -> new IllegalStateException("User should be connected"));

        try {
            List<VocabularyCollection> currentColl = fieldConfigurationService.fetchCollectionsOfPersonFieldConfiguration(person, SpatialUnit.CATEGORY_FIELD_CODE);
            if (!currentColl.isEmpty()) collections = currentColl;

            Optional<Vocabulary> currentVocab = fieldConfigurationService.fetchVocabularyOfPersonFieldConfiguration(person, SpatialUnit.CATEGORY_FIELD_CODE);
            currentVocab.ifPresent(vocab -> vocabulary = vocab);

            if(vocabulary == null && collections == null) throw new NoConfigForField(SpatialUnit.CATEGORY_FIELD_CODE);
            if(vocabulary != null && collections != null ) throw new IllegalStateException("Only one type of configuration should be set");

            if (collections != null && !collections.isEmpty()) {
                concepts = fieldService.fetchAutocomplete(collections, input);
            } else {
                concepts = fieldService.fetchAutocomplete(vocabulary, input);
            }

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
