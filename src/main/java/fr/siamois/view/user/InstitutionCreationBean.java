package fr.siamois.view.user;

import fr.siamois.view.LangBean;
import fr.siamois.view.NavBean;
import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.FailedInstitutionSaveException;
import fr.siamois.domain.models.exceptions.InstitutionAlreadyExist;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.services.PersonService;
import fr.siamois.domain.utils.CodeUtils;
import fr.siamois.domain.utils.MessageUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

/**
 * <p>This bean handles the creation of a new team</p>
 * <p>It is used to create a new team and add a manager to it</p>
 *
 * @author Julien Linget
 */
@Slf4j
@Getter
@Setter
@Component
public class InstitutionCreationBean implements Serializable {

    // Injections
    private final transient PersonService personService;
    private final LangBean langBean;
    private final UserAddBean userAddBean;
    private final transient InstitutionService institutionService;
    private final NavBean navBean;

    // Storage
    private List<Person> managers;
    private List<Person> filteredManagers;

    // Fields
    private String fTeamName;
    private String fDescription;
    private String fManagerSelectionType;
    private String fTeamDescription;
    private String fInstCode = CodeUtils.generateCode(6);

    private Person fManager;

    public InstitutionCreationBean(PersonService personService, LangBean langBean, UserAddBean userAddBean, InstitutionService institutionService, NavBean navBean) {
        this.personService = personService;
        this.langBean = langBean;
        this.userAddBean = userAddBean;
        this.institutionService = institutionService;
        this.navBean = navBean;
    }

    /**
     * Reset the variables of the bean and load all the managers in the bean
     */
    public void init() {
        loadManagers();
        fTeamName = "";
        fDescription = "";
        fManagerSelectionType = "select";
        fTeamDescription = "";
        fManager = null;
    }

    /**
     * Load all the managers in the bean
     */
    public void loadManagers() {
        managers = institutionService.findAllManagers();
    }


    /**
     * Save the team and the manager in the database
     */
    public void saveTeamAndManager() {
        if (fManagerSelectionType.equalsIgnoreCase("create")) {
            fManager = userAddBean.createUser(true);
        }

        if (fManager != null)  {
            try {
                Institution institution = new Institution();
                institution.setName(fTeamName);
                institution.setDescription(fDescription);
                institution.setManager(fManager);
                institution.setIdentifier(fInstCode);
                institutionService.createInstitution(institution);
                navBean.updateInstitutions();
                MessageUtils.displayInfoMessage(langBean, "create.team.success");
            } catch (InstitutionAlreadyExist e) {
                log.error("Institution already exists.", e);
                MessageUtils.displayErrorMessage(langBean, "commons.error.team.alreadyexist", fTeamName);
            } catch (FailedInstitutionSaveException e) {
                log.error("Failed to save team.", e);
                MessageUtils.displayErrorMessage(langBean, "commons.error.team.failedsave");
            }
        }
    }

}
