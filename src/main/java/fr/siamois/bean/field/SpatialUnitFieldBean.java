package fr.siamois.bean.field;

import fr.siamois.bean.LangBean;
import fr.siamois.bean.SessionSettingsBean;
import fr.siamois.models.exceptions.NoConfigForField;
import fr.siamois.models.exceptions.SpatialUnitAlreadyExistsException;
import fr.siamois.models.exceptions.ark.NoArkConfigException;
import fr.siamois.models.exceptions.ark.TooManyGenerationsException;
import fr.siamois.models.spatialunit.SpatialUnit;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.services.SpatialUnitService;
import fr.siamois.services.vocabulary.ConceptService;
import fr.siamois.services.vocabulary.FieldConfigurationService;
import fr.siamois.services.vocabulary.FieldService;
import fr.siamois.utils.MessageUtils;
import jakarta.faces.application.FacesMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    private final transient FieldService fieldService;
    private final LangBean langBean;
    private final SessionSettingsBean sessionSettingsBean;
    private final transient SpatialUnitService spatialUnitService;
    private final transient ConceptService conceptService;
    private final transient FieldConfigurationService fieldConfigurationService;

    // Storage
    private List<SpatialUnit> refSpatialUnits = new ArrayList<>();
    private List<String> labels;
    private List<Concept> concepts;

    // Fields
    private Concept selectedConcept = null;
    private String fName = "";
    private List<SpatialUnit> fParentsSpatialUnits = new ArrayList<>();
    private List<SpatialUnit> fChildrenSpatialUnits = new ArrayList<>();

    public SpatialUnitFieldBean(FieldService fieldService,
                                LangBean langBean,
                                SessionSettingsBean sessionSettingsBean,
                                SpatialUnitService spatialUnitService,
                                ConceptService conceptService,
                                FieldConfigurationService fieldConfigurationService) {
        this.fieldService = fieldService;
        this.langBean = langBean;
        this.sessionSettingsBean = sessionSettingsBean;
        this.spatialUnitService = spatialUnitService;
        this.conceptService = conceptService;
        this.fieldConfigurationService = fieldConfigurationService;
    }

    /**
     * Called on page rendering.
     * Reset all fields.
     */
    public void init() {
        init(new ArrayList<>(),new ArrayList<>());
        refSpatialUnits = spatialUnitService.findAllOfInstitution(sessionSettingsBean.getSelectedInstitution());
        labels = refSpatialUnits.stream()
                .map(SpatialUnit::getName)
                .toList();
        concepts = null;
        selectedConcept = null;
        fName = "";
        fParentsSpatialUnits = new ArrayList<>();
    }

    public void init(List<SpatialUnit> parents, List<SpatialUnit> children) {
        refSpatialUnits = spatialUnitService.findAllOfInstitution(sessionSettingsBean.getSelectedInstitution());
        labels = refSpatialUnits.stream()
                .map(SpatialUnit::getName)
                .toList();
        concepts = null;
        selectedConcept = null;
        fName = "";
        fParentsSpatialUnits = parents;
        fChildrenSpatialUnits = children;
    }

    /**
     * Save the spatial unit in the database.
     * Display a message if the spatial unit already exists.
     * Display a message if the spatial unit has been created.
     * @throws IllegalStateException if the collections are not defined
     */
    public String save() {

        try {
            SpatialUnit saved = spatialUnitService.save(sessionSettingsBean.getUserInfo(), fName, selectedConcept, fParentsSpatialUnits);

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
            return fieldConfigurationService.fetchAutocomplete(sessionSettingsBean.getUserInfo(), SpatialUnit.CATEGORY_FIELD_CODE, input);
        } catch (NoConfigForField e) {
            log.error(e.getMessage(), e);
            return new ArrayList<>();
        }
    }


}
