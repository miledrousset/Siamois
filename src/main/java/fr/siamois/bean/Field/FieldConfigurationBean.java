package fr.siamois.bean.Field;

import fr.siamois.infrastructure.api.ThesaurusCollectionApi;
import fr.siamois.infrastructure.api.dto.LabelDTO;
import fr.siamois.infrastructure.api.dto.VocabularyCollectionDTO;
import fr.siamois.models.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Getter
@Setter
@Component
@SessionScoped
public class FieldConfigurationBean implements Serializable {

    private final ThesaurusCollectionApi thesaurusCollectionApi;
    private final FieldConfigurationService fieldConfigurationService;

    private final AuthenticatedUserUtils userUtils = new AuthenticatedUserUtils();
    private List<VocabularyCollectionDTO> collections = new ArrayList<>();
    private final String lang = "fr";

    private String serverUrl = "https://thesaurus.mom.fr/opentheso";
    private String thesaurusId = "th221";
    private String selectedValue = "";
    private List<String> values = new ArrayList<>();

    public FieldConfigurationBean(ThesaurusCollectionApi thesaurusCollectionApi, FieldConfigurationService fieldConfigurationService) {
        this.thesaurusCollectionApi = thesaurusCollectionApi;
        this.fieldConfigurationService = fieldConfigurationService;
    }

    public String getAuthenticatedUser() {
        Person loggedUser = userUtils.getAuthenticatedUser().orElseThrow();
        return loggedUser.getUsername();
    }

    public void processForm() {
        Person loggedUser = userUtils.getAuthenticatedUser().orElseThrow();
        Optional<VocabularyCollectionDTO> opt = findSelectedVocabulary();
        if (opt.isEmpty()) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Erreur interne lors du traitement de la donnée"));
        }
        try {
            fieldConfigurationService.saveThesaurusFieldConfiguration(loggedUser,
                    SpatialUnit.CATEGORY_FIELD_CODE,
                    serverUrl,
                    thesaurusId,
                    opt.orElseThrow());

            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "La configuration a bien été enregistrée !"));
        }  catch (FailedFieldSaveException e) {
            log.error(e.getMessage(), e);
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Error", "Erreur lors de la sauvegarde de la configuration"));
        } catch (FailedFieldUpdateException e) {
            log.error(e.getMessage());
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "La configuration est identique à la précédente."));
        }

    }

    public void loadValues() {
        collections = fieldConfigurationService.fetchListOfCollection(serverUrl, thesaurusId);
        for (VocabularyCollectionDTO dto : collections) {
            String label = dto.getLabels().stream()
                    .filter(labelDTO -> labelDTO.getLang().equalsIgnoreCase(lang))
                    .map(LabelDTO::getTitle)
                    .findFirst()
                    .orElseThrow();
            values.add(label);
        }
    }

    private Optional<VocabularyCollectionDTO> findSelectedVocabulary() {
        for (VocabularyCollectionDTO dto : collections) {
            for (LabelDTO labelDTO : dto.getLabels()) {
                if (labelDTO.getLang().equalsIgnoreCase(lang) && labelDTO.getTitle().equalsIgnoreCase(selectedValue)) {
                    return Optional.of(dto);
                }
            }
        }
        return Optional.empty();
    }


}
