package fr.siamois.bean.ActionUnit;

import fr.siamois.bean.LangBean;
import fr.siamois.bean.SessionSettings;
import fr.siamois.infrastructure.api.dto.ConceptFieldDTO;
import fr.siamois.models.UserInfo;
import fr.siamois.models.actionunit.ActionUnit;
import fr.siamois.models.SpatialUnit;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.NoConfigForField;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.services.ActionUnitService;
import fr.siamois.services.vocabulary.ConceptService;
import fr.siamois.services.vocabulary.FieldConfigurationService;
import fr.siamois.services.vocabulary.FieldService;
import fr.siamois.utils.AuthenticatedUserUtils;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;


@Slf4j
@Data
@Component
@SessionScoped
public class NewActionUnitBean implements Serializable {

    // Deps
    private final ActionUnitService actionUnitService;
    private final FieldService fieldService;
    private final LangBean langBean;
    private final SessionSettings sessionSettings;
    private final FieldConfigurationService fieldConfigurationService;
    private final ConceptService conceptService;

    // Local
    private ActionUnit actionUnit;
    private List<Concept> concepts;
    private Concept fieldType = null;
    private Concept typeParent;


    public NewActionUnitBean(ActionUnitService actionUnitService, FieldService fieldService, LangBean langBean, SessionSettings sessionSettings, FieldConfigurationService fieldConfigurationService, ConceptService conceptService) {
        this.actionUnitService = actionUnitService;
        this.fieldService = fieldService;
        this.langBean = langBean;
        this.sessionSettings = sessionSettings;
        this.fieldConfigurationService = fieldConfigurationService;
        this.conceptService = conceptService;
    }


    public String save() {
        try {
            Person author = sessionSettings.getAuthenticatedUser();
            actionUnit.setAuthor(author);
            actionUnit.setBeginDate(OffsetDateTime.now()); // todo : implement
            actionUnit.setEndDate(OffsetDateTime.now());  // todo : implement

            this.actionUnit = actionUnitService.save(actionUnit, fieldType);

            return "/pages/actionUnit/actionUnit?faces-redirect=true&id=" + this.actionUnit.getId().toString();

        } catch (RuntimeException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            langBean.msg("actionunit.creationfailed", this.actionUnit.getName())));

            log.error("Error while saving: {}", e.getMessage());
            // todo : add error message
            return null;
        }
    }

    /**
     * Fetch the autocomplete results on API for the type field and add them to the list of concepts.
     *
     * @param input the input of the user
     * @return the list of concepts that match the input to display in the autocomplete
     */
    public List<Concept> completeActionUnitType(String input) {
        UserInfo info = sessionSettings.getUserInfo();
        try {
            concepts = conceptService.fetchAllValues(info, ActionUnit.TYPE_FIELD_CODE);
        } catch (NoConfigForField e) {
            log.error(e.getMessage(), e);
        }
        return concepts;
    }

    @PostConstruct
    public void init() {
        fieldType = null;
        actionUnit = new ActionUnit();
        actionUnit.setName("Nouvelle action");
    }

    public void reinit(SpatialUnit spatialUnit) {
        this.actionUnit.setSpatialUnit(spatialUnit);
    }


}
