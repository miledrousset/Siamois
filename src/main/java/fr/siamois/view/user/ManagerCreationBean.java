package fr.siamois.view.user;

import fr.siamois.view.LangBean;
import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.Team;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.services.PersonService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>This bean handles the creation of a new manager</p>
 * <p>It is used to create a new manager and add it to a team</p>
 *
 * @author Julien Linget
 */
@Getter
@Setter
@Slf4j
@Component
public class ManagerCreationBean implements Serializable {
    // Injections
    private final transient PersonService personService;
    private final LangBean langBean;
    private final UserAddBean userAddBean;
    private final transient InstitutionService institutionService;

    // Storage
    transient List<Institution> refInstitutions = new ArrayList<>();
    transient List<Institution> filteredInstitutions = new ArrayList<>();

    // Fields
    private transient List<Team> vTeams = new ArrayList<>();

    public ManagerCreationBean(PersonService personService, LangBean langBean, UserAddBean userAddBean, InstitutionService institutionService) {
        this.personService = personService;
        this.langBean = langBean;
        this.userAddBean = userAddBean;
        this.institutionService = institutionService;
    }

    /**
     * Reset the variables of the bean and load all the teams in the bean
     */
    public void init() {
        resetVariables();
        refInstitutions = institutionService.findAll();
    }

    /**
     * Reset the variables of the bean
     */
    private void resetVariables() {
        refInstitutions = new ArrayList<>();
        filteredInstitutions = new ArrayList<>();
        userAddBean.resetVariables();
        vTeams = new ArrayList<>();
    }

    /**
     * Create a new manager in the database
     */
    public void createUser() {
        userAddBean.createUser(true);
    }

}
