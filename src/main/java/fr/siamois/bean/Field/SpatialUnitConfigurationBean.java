package fr.siamois.bean.Field;

import fr.siamois.bean.LangBean;
import fr.siamois.models.SpatialUnit;
import fr.siamois.models.Team;
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
    private final LangBean langBean;

    // Configuration storage
    private List<VocabularyCollection> collections = new ArrayList<>();
    private List<Vocabulary> vocabularies = new ArrayList<>();
    private Vocabulary selectedVocab = null;
    private Map<String, String> labels = new HashMap<>();
    private List<VocabularyCollection> cacheSelectedGroups = new ArrayList<>();
    private List<VocabularyCollection> cachedGroups = new ArrayList<>();
    private List<String> fieldCodes = List.of(SpatialUnit.CATEGORY_FIELD_CODE, Team.ROLE_FIELD_CODE);

    // Fields
    private boolean selectEntireThesaurus = false;
    private String serverUrl = "";
    private List<VocabularyCollection> selectedGroups = new ArrayList<>();
    private String selectedThesaurus = "";
    private String selectedFieldCode = "";

    public SpatialUnitConfigurationBean(FieldConfigurationService fieldConfigurationService, LangBean langBean) {
        this.fieldConfigurationService = fieldConfigurationService;
        this.langBean = langBean;
    }

    public void init() {
        collections = new ArrayList<>();
        vocabularies = new ArrayList<>();
        selectedVocab = null;
        labels = new HashMap<>();
        cacheSelectedGroups = new ArrayList<>();
        cachedGroups = new ArrayList<>();

        selectEntireThesaurus = false;
        serverUrl = "";
        selectedGroups = new ArrayList<>();
        selectedThesaurus = "";
    }

    /**
     * Load the existing configuration on the page.
     * If it's a thesaurus configuration, load the thesaurus list, the selected thesaurus and the empty collection.
     * If it's collections, load the collections list, the selected collections and the selected thesaurus.
     */
    public void onLoad() {
        init();
        cacheSelectedGroups = new ArrayList<>(selectedGroups);
        Person loggedUser = AuthenticatedUserUtils.getAuthenticatedUser().orElseThrow();
        Optional<Vocabulary> optionalVocabulary = fieldConfigurationService.fetchVocabularyOfPersonFieldConfiguration(loggedUser, SpatialUnit.CATEGORY_FIELD_CODE);
        if (optionalVocabulary.isPresent()) {
            loadThesaurusOnlyConfiguration(optionalVocabulary.get());
        } else {
            loadGroupsConfiguration(loggedUser);
        }

    }

    /**
     * Load the existing groups if the setup is already done.
     * @param loggedUser The authenticated user.
     */
    private void loadGroupsConfiguration(Person loggedUser) {
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

    /**
     * Load the configuration of the Thesaurus only if the setup is already done.
     * @param vocabulary The vocabulary to load.
     */
    private void loadThesaurusOnlyConfiguration(Vocabulary vocabulary) {
        serverUrl = vocabulary.getBaseUri();

        loadThesaurusValue();

        selectedVocab = vocabulary;
    }

    /**
     * Load the collections in the selected Thesaurus in the matching field for selection.
     * On load, clears the collections list and the labels map and adds the empty collection to the list.
     */
    public void loadGroupValue() {
        try {
            selectedVocab = getSelectedVocab().orElseThrow();
            selectedVocab = fieldConfigurationService.saveVocabularyIfNotExists(selectedVocab);

            FieldConfigurationService.VocabularyCollectionsAndLabels result = fieldConfigurationService.fetchCollections(langBean.getLanguageCode(), selectedVocab);

            if (result.collections().isEmpty()) {
                collections = new ArrayList<>();
                selectEntireThesaurus = true;
                return;
            }

            List<VocabularyCollection> savedCollections = result.collections();
            List<String> localisedLabels = result.localisedLabels();
            labels = new HashMap<>();
            collections = new ArrayList<>();
            selectEntireThesaurus = false;

            for (int i = 0; i < localisedLabels.size(); i++)
                labels.put(savedCollections.get(i).getExternalId(), localisedLabels.get(i));

            collections.addAll(savedCollections);



        } catch (ClientSideErrorException e) {
            log.error(e.getMessage(), e);
            displayErrorMessage("Serveur introuvable ou invalide");
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
     * Save or update the configuration.
     * Refresh the groups list if the selected groups are empty or contain the empty collection.
     */
    public void processForm() {
        Person loggedUser = AuthenticatedUserUtils.getAuthenticatedUser().orElseThrow();

        if (userHasSelectedEntireThesaurus()) {
            try {
                fieldConfigurationService.saveThesaurusFieldConfiguration(loggedUser, SpatialUnit.CATEGORY_FIELD_CODE, selectedVocab);

                displayInfoMessage("La configuration a bien été enregistrée");
            } catch (FailedFieldUpdateException e) {
                log.error(e.getMessage());
                displayInfoMessage("La configuration est identique à la précédente.");
            }
            return;
        }

        List<VocabularyCollection> savedVocabColl = new ArrayList<>();
        for (VocabularyCollection collection : selectedGroups) {
            savedVocabColl.add(fieldConfigurationService.saveVocabularyCollectionIfNotExists(collection));
        }

        try {
            fieldConfigurationService.saveThesaurusCollectionFieldConfiguration(loggedUser,
                    SpatialUnit.CATEGORY_FIELD_CODE,
                    savedVocabColl);

            displayInfoMessage("La configuration a bien été enregistrée");

        } catch (FailedFieldUpdateException e) {
            displayInfoMessage("La configuration est identique à la précédente.");
        }

    }

    /**
     * Check if the user has selected the entire thesaurus.
     * @return True if the user has selected the entire thesaurus, false otherwise.
     */
    private boolean userHasSelectedEntireThesaurus() {
        return selectEntireThesaurus;
    }

    /**
     * Display an error message on the page.
     * @param detail The message to display.
     */
    private static void displayInfoMessage(String detail) {
        displayMessage(FacesMessage.SEVERITY_INFO, "Information", detail);
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
            displayErrorMessage("L'URL du serveur est invalide.");
            return;
        }

        if (serverUrl.endsWith("/")) {
            serverUrl = serverUrl.substring(0,  serverUrl.length() - 1);
        }

        collections = new ArrayList<>();
        vocabularies = new ArrayList<>();
        selectedVocab = null;
        selectedThesaurus = null;

        List<Vocabulary> result = fieldConfigurationService.fetchAllPublicThesaurus(langBean.getLanguageCode(), serverUrl);
        vocabularies.addAll(result);

    }

    /**
     * Display an error message on the page.
     * @param s The message to display.
     */
    private void displayErrorMessage(String s) {
        displayMessage(FacesMessage.SEVERITY_ERROR, "Erreur", s);
    }

    /**
     * Cache the selected groups and remove all the selected groups.
     */
    public void removeAllSelectedGroups() {
        if (collections.isEmpty()) selectEntireThesaurus = true;
        if (selectEntireThesaurus) {
            cacheSelectedGroups = new ArrayList<>(selectedGroups);
            cachedGroups = new ArrayList<>(collections);
            selectedGroups.clear();
        } else {
            selectedGroups = new ArrayList<>(cacheSelectedGroups);
            collections = new ArrayList<>(cachedGroups);
        }
    }


}
