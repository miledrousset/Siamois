package fr.siamois.bean.Field;

import fr.siamois.bean.LangBean;
import fr.siamois.bean.SessionSettings;
import fr.siamois.bean.converter.ConceptConverter;
import fr.siamois.models.SpatialUnit;
import fr.siamois.models.exceptions.NoConfigForField;
import fr.siamois.models.exceptions.SpatialUnitAlreadyExistsException;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.services.SpatialUnitService;
import fr.siamois.services.vocabulary.ConceptService;
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
    private final LangBean langBean;
    private final SessionSettings sessionSettings;
    private final SpatialUnitService spatialUnitService;
    private final ConceptService conceptService;
    private final ConceptConverter conceptConverter;
    private final FieldConfigurationService fieldConfigurationService;

    // Storage
    private List<SpatialUnit> refSpatialUnits = new ArrayList<>();
    private List<String> labels;
    private List<Concept> concepts;

    // Fields
    private Concept selectedConcept = null;
    private String fName = "";
    private List<SpatialUnit> fParentsSpatialUnits = new ArrayList<>();

    public SpatialUnitFieldBean(FieldService fieldService,
                                LangBean langBean,
                                SessionSettings sessionSettings,
                                SpatialUnitService spatialUnitService,
                                ConceptService conceptService,
                                ConceptConverter conceptConverter, FieldConfigurationService fieldConfigurationService) {
        this.fieldService = fieldService;
        this.langBean = langBean;
        this.sessionSettings = sessionSettings;
        this.spatialUnitService = spatialUnitService;
        this.conceptService = conceptService;
        this.conceptConverter = conceptConverter;
        this.fieldConfigurationService = fieldConfigurationService;
    }

    /**
     * Called on page rendering.
     * Reset all fields.
     */
    public void init() {
        refSpatialUnits = spatialUnitService.findAllOfInstitution(sessionSettings.getSelectedInstitution());
        labels = refSpatialUnits.stream()
                .map(SpatialUnit::getName)
                .collect(Collectors.toList());
        concepts = null;
        selectedConcept = null;
        fName = "";
        fParentsSpatialUnits = new ArrayList<>();
    }

    public void init(List<SpatialUnit> parents) {
        refSpatialUnits = spatialUnitService.findAllOfInstitution(sessionSettings.getSelectedInstitution());
        labels = refSpatialUnits.stream()
                .map(SpatialUnit::getName)
                .collect(Collectors.toList());
        concepts = null;
        selectedConcept = null;
        fName = "";
        fParentsSpatialUnits = parents;
    }

    /**
     * Save the spatial unit in the database.
     * Display a message if the spatial unit already exists.
     * Display a message if the spatial unit has been created.
     * @throws IllegalStateException if the collections are not defined
     */
    public String save() {

        try {
            SpatialUnit saved = spatialUnitService.save(sessionSettings.getUserInfo(), fName, selectedConcept, fParentsSpatialUnits);
            MessageUtils.displayInfoMessage(langBean, "spatialunit.created", saved.getName());

            return "/pages/spatialUnit/spatialUnit?faces-redirect=true&id=" + saved.getId().toString();
        } catch (SpatialUnitAlreadyExistsException e) {
            log.error(e.getMessage(), e);
            MessageUtils.displayErrorMessage(langBean, "commons.error.spatialunit.alreadyexist", fName);
            return null;
        }
    }

    /**
     * Fetch the autocomplete results on API for the category field and add them to the list of concepts.
     * @param input the input of the user
     * @return the list of concepts that match the input to display in the autocomplete
     */
    public List<Concept> completeCategory(String input) {
        try {
            return fieldConfigurationService.fetchAutocomplete(sessionSettings.getUserInfo(), SpatialUnit.CATEGORY_FIELD_CODE, input);
        } catch (NoConfigForField e) {
            log.error(e.getMessage(), e);
            return new ArrayList<>();
        }
    }


}
