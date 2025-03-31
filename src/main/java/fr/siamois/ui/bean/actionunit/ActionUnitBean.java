package fr.siamois.ui.bean.actionunit;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.actionunit.ActionCode;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.actionunit.ActionUnitNotFoundException;
import fr.siamois.domain.models.exceptions.vocabulary.NoConfigForFieldException;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.actionunit.ActionUnitService;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Data
@Component
@SessionScoped
public class ActionUnitBean implements Serializable {

    // Deps
    private final transient ActionUnitService actionUnitService;
    private final LangBean langBean;
    private final SessionSettingsBean sessionSettingsBean;
    private final transient FieldConfigurationService fieldConfigurationService;
    private final transient FieldService fieldService;
    private final RedirectBean redirectBean;

    // Local
    private ActionUnit actionUnit;
    private String actionUnitErrorMessage;
    private Long id;  // ID of the action unit requested

    // For entering new code
    private ActionCode newCode;
    private Integer newCodeIndex; // Index of the new code, if primary: 0, otherwise 1 to N
    // (but corresponds to 0 to N-1 in secondary code list)

    // Field related
    private Boolean editType;
    private Concept fType;

    private transient List<ActionCode> secondaryActionCodes;


    public ActionUnitBean(ActionUnitService actionUnitService, LangBean langBean, SessionSettingsBean sessionSettingsBean, FieldConfigurationService fieldConfigurationService, FieldService fieldService, RedirectBean redirectBean) {
        this.actionUnitService = actionUnitService;
        this.langBean = langBean;
        this.sessionSettingsBean = sessionSettingsBean;
        this.fieldConfigurationService = fieldConfigurationService;
        this.fieldService = fieldService;
        this.redirectBean = redirectBean;
    }

    @PostConstruct
    public void postConstruct() {
        editType = false;
    }

    /**
     * Fetch the autocomplete results on API for the type field and add them to the list of concepts.
     *
     * @param input the input of the user
     * @return the list of concepts that match the input to display in the autocomplete
     */
    public List<Concept> completeActionUnitType(String input) {
        UserInfo info = sessionSettingsBean.getUserInfo();
        List<Concept> concepts = Collections.emptyList();
        try {
            concepts = fieldConfigurationService.fetchAutocomplete(info, ActionUnit.TYPE_FIELD_CODE, input);
        } catch (NoConfigForFieldException e) {
            log.error(e.getMessage(), e);
        }
        return concepts;
    }

    public String getUrlForActionUnitTypeFieldCode() {
        return fieldConfigurationService.getUrlForFieldCode(sessionSettingsBean.getUserInfo(), ActionUnit.TYPE_FIELD_CODE);
    }



    public void save() {
        try {
            Person author = sessionSettingsBean.getAuthenticatedUser();
            actionUnit.setLastModifiedBy(author);

            this.actionUnit = actionUnitService.save(actionUnit, secondaryActionCodes, sessionSettingsBean.getUserInfo());

            // Display message
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_INFO,
                            "Info",
                            langBean.msg("actionunit.created", this.actionUnit.getName())));

        } catch (RuntimeException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            langBean.msg("actionunit.creationfailed", this.actionUnit.getName())));

            log.error("Error while saving: {}", e.getMessage());
        }
    }



    public void init() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            // reinit
            actionUnitErrorMessage = null;
            actionUnit = null;
            newCode = new ActionCode();
            secondaryActionCodes = new ArrayList<>();
            // Get the requested action from DB
            try {
                if (id != null) {
                    actionUnit = actionUnitService.findById(id);
                    secondaryActionCodes = new ArrayList<>(actionUnit.getSecondaryActionCodes());
                    fType = this.actionUnit.getType();
                } else {
                    log.error("The Action Unit page should not be accessed without ID or by direct page path");
                    redirectBean.redirectTo(HttpStatus.NOT_FOUND);
                }
            } catch (ActionUnitNotFoundException e) {
                log.error("Action unit with id {} not found", id);
                redirectBean.redirectTo(HttpStatus.NOT_FOUND);
            } catch (RuntimeException e) {
                this.actionUnitErrorMessage = "Failed to load action unit: " + e.getMessage();
                redirectBean.redirectTo(HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }


    }


}
