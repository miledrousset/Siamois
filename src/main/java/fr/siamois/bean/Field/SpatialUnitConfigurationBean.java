package fr.siamois.bean.Field;

import fr.siamois.models.SpatialUnit;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.api.ClientSideErrorException;
import fr.siamois.models.exceptions.field.FailedFieldUpdateException;
import fr.siamois.models.vocabulary.Vocabulary;
import fr.siamois.models.vocabulary.VocabularyCollection;
import fr.siamois.services.FieldConfigurationService;
import fr.siamois.utils.AuthenticatedUserUtils;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.*;

/**
 * <p>This class is a bean that handles the configuration of the field Spatial Unit in the application.</p>
 * @author Julien Linget
 */
@Slf4j
@Getter
@Setter
@Component
@SessionScoped
public class SpatialUnitConfigurationBean implements Serializable {

    // Dependencies
    private final FieldConfigurationService fieldConfigurationService;

    // Configuration storage
    private List<VocabularyCollection> collections = new ArrayList<>();
    private List<Vocabulary> vocabularies = new ArrayList<>();
    private Vocabulary selectedVocab = null;
    private Map<String, String> labels = new HashMap<>();
    private final String lang = "fr";
    private static final String EMPTY_LABEL = "Tout le thésaurus";
    private static final String EMPTY_ID = " ";
    private final VocabularyCollection emptyVocab;

    // Fields
    private String serverUrl = "";
    private List<VocabularyCollection> selectedGroups = new ArrayList<>();
    private String selectedThesaurus = "";

    public SpatialUnitConfigurationBean(FieldConfigurationService fieldConfigurationService) {
        this.fieldConfigurationService = fieldConfigurationService;

        emptyVocab = new VocabularyCollection();
        emptyVocab.setId(-1L);
        emptyVocab.setExternalId(EMPTY_ID);
    }

    /**
     * Load the existing configuration of the field, if this configuration exist.
     */
    public void onLoad() {
        Person loggedUser = AuthenticatedUserUtils.getAuthenticatedUser().orElseThrow();
        Optional<Vocabulary> optionalVocabulary = fieldConfigurationService.fetchVocabularyOfPersonFieldConfiguration(loggedUser, SpatialUnit.CATEGORY_FIELD_CODE);
        if (optionalVocabulary.isPresent()) {
            Vocabulary  vocabulary = optionalVocabulary.get();
            serverUrl = vocabulary.getBaseUri();

            loadThesaurusValue();

            selectedVocab = vocabulary;

            collections.add(emptyVocab);
            labels.put(EMPTY_ID, "Tout le thésaurus");

            selectedGroups.add(emptyVocab);

        } else {
            List<VocabularyCollection> alreadySelectedGroups = fieldConfigurationService.fetchCollectionsOfPersonFieldConfiguration(loggedUser, SpatialUnit.CATEGORY_FIELD_CODE);
            if (!alreadySelectedGroups.isEmpty()) {
                Vocabulary vocabulary = alreadySelectedGroups.get(0).getVocabulary();
                serverUrl = vocabulary.getBaseUri();
                selectedGroups = alreadySelectedGroups;

                loadThesaurusValue();

                selectedVocab = vocabulary;
                selectedThesaurus = selectedVocab.getExternalVocabularyId();

                loadGroupValue();
            }
        }

    }

    /**
     * Load the collections in the selected Thesaurus in the matching field for selection.
     */
    public void loadGroupValue() {
        try {
            selectedVocab = getSelectedVocab().orElseThrow();
            selectedVocab = fieldConfigurationService.saveVocabularyIfNotExists(selectedVocab);

            FieldConfigurationService.VocabularyCollectionsAndLabels result = fieldConfigurationService.fetchCollections(lang, selectedVocab);

            List<VocabularyCollection> savedCollections = result.collections();
            List<String> localisedLabels = result.localisedLabels();
            labels = new HashMap<>();
            collections = new ArrayList<>();

            for (int i = 0; i < localisedLabels.size(); i++)
                labels.put(savedCollections.get(i).getExternalId(), localisedLabels.get(i));

            labels.put(EMPTY_ID, EMPTY_LABEL);
            VocabularyCollection empty = new VocabularyCollection();
            empty.setId(-1L);
            empty.setExternalId(EMPTY_ID);

            collections.add(empty);

            collections.addAll(savedCollections);



        } catch (ClientSideErrorException e) {
            log.error(e.getMessage(), e);
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Serveur introuvable ou invalide"));
        }
    }

    /**
     * Get the username of the current authenticated user.
     * @return The username of the User
     * @throws IllegalStateException Throws when no user is authenticated as this page should only be visible to authenticated user.
     */
    public String getAuthenticatedUser() {
        Person loggedUser = AuthenticatedUserUtils.getAuthenticatedUser().orElseThrow(() -> new IllegalStateException("No user logged in"));
        return loggedUser.getUsername();
    }

    /**
     * Save or update the input collection configuration. Displays a message depending on the success or failure of this operation.
     */
    public void processForm() {
        Person loggedUser = AuthenticatedUserUtils.getAuthenticatedUser().orElseThrow();

        if (selectedGroups.isEmpty() || selectedGroups.stream().anyMatch(elt -> elt == emptyVocab)) {
            selectedGroups = new ArrayList<>();
            selectedGroups.add(collections
                    .stream()
                    .filter(elt -> elt.getExternalId().equalsIgnoreCase(EMPTY_ID))
                    .findFirst()
                    .orElseThrow());
            try {
                fieldConfigurationService.saveThesaurusFieldConfiguration(loggedUser, SpatialUnit.CATEGORY_FIELD_CODE, selectedVocab);

                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "La configuration a bien été enregistrée"));

                return;
            } catch (FailedFieldUpdateException e) {
                log.error(e.getMessage());
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "La configuration est identique à la précédente."));
            }
        }

        List<VocabularyCollection> savedVocabColl = new ArrayList<>();
        for (VocabularyCollection collection : selectedGroups) {
            savedVocabColl.add(fieldConfigurationService.saveVocabularyCollectionIfNotExists(collection));
        }

        try {
            fieldConfigurationService.saveThesaurusCollectionFieldConfiguration(loggedUser,
                    SpatialUnit.CATEGORY_FIELD_CODE,
                    savedVocabColl);

            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "La configuration a bien été enregistrée"));

        } catch (FailedFieldUpdateException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "La configuration est identique à la précédente."));
        }

    }

    /**
     * Search for the selected vocabulary by its id input in the field.
     * @return An empty optional of the ID is invalid. Returns the vocabulary otherwise.
     */
    private Optional<Vocabulary> getSelectedVocab() {
        return vocabularies.stream()
                .filter(vocabulary -> vocabulary.getExternalVocabularyId().equalsIgnoreCase(selectedThesaurus))
                .findFirst();
    }

    /**
     * Load the list of vocabularies from the server URL input in the server URL field.
     */
    public void loadThesaurusValue() {

        if (!serverUrl.startsWith("http") && !serverUrl.startsWith("https")) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Erreur", "L'URL du serveur est invalide."));
            return;
        }

        if (serverUrl.endsWith("/")) {
            serverUrl = serverUrl.substring(0,  serverUrl.length() - 1);
        }

        collections = new ArrayList<>();
        vocabularies = new ArrayList<>();
        selectedVocab = null;
        selectedThesaurus = null;

        List<Vocabulary> result = fieldConfigurationService.fetchAllPublicThesaurus(lang, serverUrl);
        vocabularies.addAll(result);

    }


}
