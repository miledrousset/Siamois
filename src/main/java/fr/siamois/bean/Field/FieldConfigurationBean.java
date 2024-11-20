package fr.siamois.bean.Field;

import fr.siamois.infrastructure.api.dto.LabelDTO;
import fr.siamois.infrastructure.api.dto.VocabularyCollectionDTO;
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

@Slf4j
@Getter
@Setter
@Component
@SessionScoped
public class FieldConfigurationBean implements Serializable {

    private final FieldConfigurationService fieldConfigurationService;

    private final AuthenticatedUserUtils userUtils = new AuthenticatedUserUtils();
    private List<VocabularyCollection> collections = new ArrayList<>();
    private Vocabulary selectedVocab;
    private Map<String, String> labels = new HashMap<>();
    private final String lang = "fr";

    private String serverUrl = "";
    private String thesaurusId = "";
    private String selectedValue = "";

    public FieldConfigurationBean(FieldConfigurationService fieldConfigurationService) {
        this.fieldConfigurationService = fieldConfigurationService;
    }

    public void onLoad() {
        Person loggedUser = userUtils.getAuthenticatedUser().orElseThrow();
        Optional<VocabularyCollection> opt = fieldConfigurationService.fetchPersonFieldConfiguration(loggedUser, SpatialUnit.CATEGORY_FIELD_CODE);
        if (opt.isPresent()) {
            VocabularyCollection vocabularyCollection = opt.get();
            selectedValue = vocabularyCollection.getExternalId();
            selectedVocab = vocabularyCollection.getVocabulary();
            serverUrl = selectedVocab.getBaseUri();
            thesaurusId = selectedVocab.getExternalVocabularyId();
            loadGroupValue();
        }
    }

    public void loadGroupValue() {
        try {
            Optional<Vocabulary> optVocab = fieldConfigurationService.fetchAndSaveThesaurus(lang, serverUrl, thesaurusId);
            if (optVocab.isEmpty()) {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Thesaurus introuvable ou invalide"));
            } else {
                selectedVocab = optVocab.get();
            }

            FieldConfigurationService.VocabularyCollectionsAndLabels result = fieldConfigurationService.fetchAndSaveCollections(lang, selectedVocab);

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

    public String getAuthenticatedUser() {
        Person loggedUser = userUtils.getAuthenticatedUser().orElseThrow();
        return loggedUser.getUsername();
    }

    public void processForm() {
        Person loggedUser = userUtils.getAuthenticatedUser().orElseThrow();

        Optional<VocabularyCollection> optSelected = getSelectedCollection();

        if (optSelected.isEmpty()) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Erreur interne lors du traitement de la donnée."));
            return;
        }

        try {
            fieldConfigurationService.saveThesaurusFieldConfiguration(loggedUser,
                    SpatialUnit.CATEGORY_FIELD_CODE,
                    optSelected.get());

            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "La configuration a bien été enregistrée !"));
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

    private Optional<VocabularyCollection> getSelectedCollection() {
        return collections.stream()
                .filter(vocabularyCollection -> vocabularyCollection.getExternalId().equalsIgnoreCase(selectedValue))
                .findFirst();
    }


}
