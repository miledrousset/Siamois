package fr.siamois.ui.bean.actionunit;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.vocabulary.NoConfigForFieldException;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.domain.services.vocabulary.FieldService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.RedirectBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
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
    private final transient ActionUnitService actionUnitService;
    private final transient FieldService fieldService;
    private final LangBean langBean;
    private final SessionSettingsBean sessionSettingsBean;
    private final transient FieldConfigurationService fieldConfigurationService;
    private final transient ConceptService conceptService;
    private final RedirectBean redirectBean;

    // Local
    private ActionUnit actionUnit;
    private transient List<Concept> concepts;
    private Concept fieldType = null;
    private Concept typeParent;


    public NewActionUnitBean(ActionUnitService actionUnitService,
                             FieldService fieldService,
                             LangBean langBean,
                             SessionSettingsBean sessionSettingsBean,
                             FieldConfigurationService fieldConfigurationService,
                             ConceptService conceptService,
                             RedirectBean redirectBean) {
        this.actionUnitService = actionUnitService;
        this.fieldService = fieldService;
        this.langBean = langBean;
        this.sessionSettingsBean = sessionSettingsBean;
        this.fieldConfigurationService = fieldConfigurationService;
        this.conceptService = conceptService;
        this.redirectBean = redirectBean;
    }


    public boolean save() {
        try {
            Person author = sessionSettingsBean.getAuthenticatedUser();
            actionUnit.setAuthor(author);
            actionUnit.setBeginDate(OffsetDateTime.now()); // todo : implement
            actionUnit.setEndDate(OffsetDateTime.now());  // todo : implement

            this.actionUnit = actionUnitService.save(sessionSettingsBean.getUserInfo() ,actionUnit, fieldType);

            redirectBean.redirectTo("/actionunit/" + actionUnit.getId());
            return true;

        } catch (RuntimeException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            langBean.msg("actionunit.creationfailed", this.actionUnit.getName())));

            log.error("Error while saving: {}", e.getMessage());
            // todo : add error message
            return false;
        }
    }

    /**
     * Fetch the autocomplete results on API for the type field and add them to the list of concepts.
     *
     * @param input the input of the user
     * @return the list of concepts that match the input to display in the autocomplete
     */
    public List<Concept> completeActionUnitType(String input) {
        UserInfo info = sessionSettingsBean.getUserInfo();
        try {
            concepts = fieldConfigurationService.fetchAutocomplete(info, ActionUnit.TYPE_FIELD_CODE, input);
        } catch (NoConfigForFieldException e) {
            log.error(e.getMessage(), e);
        }
        return concepts;
    }

    public void generateRandomActionUnitIdentifier() {
        actionUnit.setIdentifier("2025");
    }

    @PostConstruct
    public void init() {
        fieldType = null;
        actionUnit = new ActionUnit();
        actionUnit.setName("Nouvelle action");
    }

    public void reinit(SpatialUnit spatialUnit) {
        init();
        this.actionUnit.setSpatialUnit(spatialUnit);
        this.actionUnit.setCreatedByInstitution(spatialUnit.getCreatedByInstitution());
    }


}
