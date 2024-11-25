package fr.siamois.bean.Field;

import fr.siamois.models.auth.Person;
import fr.siamois.models.SpatialUnit;
import fr.siamois.models.exceptions.api.ClientSideErrorException;
import fr.siamois.models.vocabulary.Vocabulary;
import fr.siamois.models.vocabulary.VocabularyCollection;
import fr.siamois.models.exceptions.field.FailedFieldSaveException;
import fr.siamois.models.exceptions.field.FailedFieldUpdateException;
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
public class FieldConfigurationBean implements Serializable {

    private final FieldConfigurationService fieldConfigurationService;

    private final AuthenticatedUserUtils userUtils = new AuthenticatedUserUtils();
    private List<VocabularyCollection> collections = new ArrayList<>();
    private List<Vocabulary> vocabularies = new ArrayList<>();
    private Vocabulary selectedVocab = null;
    private Map<String, String> labels = new HashMap<>();
    private final String lang = "fr";

    private String serverUrl = "";
    private String selectedValue = "";
    private String selectedThesaurus = "";

    public FieldConfigurationBean(FieldConfigurationService fieldConfigurationService) {
        this.fieldConfigurationService = fieldConfigurationService;
    }

    /**
     * Load the existing configuration of the field, if this configuration exist.
     */
    public void onLoad() {
        Person loggedUser = userUtils.getAuthenticatedUser().orElseThrow();
        Optional<VocabularyCollection> opt = fieldConfigurationService.fetchPersonFieldConfiguration(loggedUser, SpatialUnit.CATEGORY_FIELD_CODE);
        if (opt.isPresent()) {
            VocabularyCollection vocabularyCollection = opt.get();
            serverUrl = vocabularyCollection.getVocabulary().getBaseUri();
            selectedValue = vocabularyCollection.getExternalId();

            loadThesaurusValue();

            selectedVocab = vocabularyCollection.getVocabulary();
            selectedThesaurus = selectedVocab.getExternalVocabularyId();

            loadGroupValue();

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

            for (int i = 0; i < localisedLabels.size(); i++)
                labels.put(savedCollections.get(i).getExternalId(), localisedLabels.get(i));

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
     * @throws NoSuchElementException Throws when no user is authenticated as this page should only be visible to authenticated user.
     */
    public String getAuthenticatedUser() {
        Person loggedUser = userUtils.getAuthenticatedUser().orElseThrow();
        return loggedUser.getUsername();
    }

    /**
     * Save or update the input collection configuration. Displays a message depending on the success or failure of this operation.
     */
    public void processForm() {
        Person loggedUser = userUtils.getAuthenticatedUser().orElseThrow();

        Optional<VocabularyCollection> optSelected = getSelectedCollectionId();

        if (optSelected.isEmpty()) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Erreur interne lors du traitement de la donnée."));
            return;
        }

        try {
            VocabularyCollection collection = fieldConfigurationService.saveVocabularyCollectionIfNotExists(optSelected.get());

            fieldConfigurationService.saveThesaurusFieldConfiguration(loggedUser,
                    SpatialUnit.CATEGORY_FIELD_CODE,
                    collection);

            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "La configuration a bien été enregistrée"));
        } catch (FailedFieldSaveException e) {
            log.error(e.getMessage(), e);
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Error", "Erreur lors de la sauvegarde de la configuration"));
        } catch (FailedFieldUpdateException e) {
            log.error(e.getMessage());
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "La configuration est identique à la précédente."));
        }

    }

    /**
     * Search for the selected collection by it's id input in the field.
     * @return An empty optional of the ID is invalid. Returns the collection otherwise.
     */
    private Optional<VocabularyCollection> getSelectedCollectionId() {
        return collections.stream()
                .filter(vocabularyCollection -> vocabularyCollection.getExternalId().equalsIgnoreCase(selectedValue))
                .findFirst();
    }

    /**
     * Search for the selected vocabulary by it's id input in the field.
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
