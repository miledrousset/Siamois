package fr.siamois.bean.Field;

import fr.siamois.infrastructure.api.dto.ConceptFieldDTO;
import fr.siamois.models.SpatialUnit;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.NoCollectionForFieldException;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.models.vocabulary.VocabularyCollection;
import fr.siamois.services.FieldConfigurationService;
import fr.siamois.services.FieldService;
import fr.siamois.utils.AuthenticatedUserUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
    private VocabularyCollection vocabularyCollection = null;

    // Fields
    private Concept selectedConcept = null;
    private String fName = "";
    private String fExtent = "";
    private String fCategory = "";
    private List<SpatialUnit> fParentsSpatialUnits = new ArrayList<>();
    private List<SpatialUnit> fChildrensSpatialUnits = new ArrayList<>();

    public SpatialUnitFieldBean(FieldService fieldService, FieldConfigurationService fieldConfigurationService) {
        this.fieldService = fieldService;
        this.fieldConfigurationService = fieldConfigurationService;
    }

    public void save() {
        log.trace("Data sent :");
        log.trace(fName);
        log.trace(fExtent);
        log.trace(fCategory);
        log.trace(fParentsSpatialUnits.toString());
        log.trace(fChildrensSpatialUnits.toString());
    }

    public List<String> completeCategory(String input) {
        Person person = AuthenticatedUserUtils.getAuthenticatedUser().orElseThrow(() -> new IllegalStateException("User should be connected"));

        try {
            if (vocabularyCollection == null) {
                vocabularyCollection = fieldConfigurationService.fetchPersonFieldConfiguration(person, SpatialUnit.CATEGORY_FIELD_CODE)
                        .orElseThrow(() -> new NoCollectionForFieldException(SpatialUnit.CATEGORY_FIELD_CODE));
            }

            concepts = fieldService.fetchAutocomplete(vocabularyCollection, input);

            return concepts.stream()
                    .map(ConceptFieldDTO::getLabel)
                    .collect(Collectors.toList());

        } catch (NoCollectionForFieldException e) {
            log.error("No collection for field " + SpatialUnit.CATEGORY_FIELD_CODE);
            return new ArrayList<>();
        }
    }

}
