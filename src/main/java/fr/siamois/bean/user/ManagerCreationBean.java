package fr.siamois.bean.user;

import fr.siamois.bean.LangBean;
import fr.siamois.models.Team;
import fr.siamois.models.auth.Person;
import fr.siamois.services.PersonService;
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
    private final PersonService personService;
    private final LangBean langBean;
    private final UserAddBean userAddBean;

    // Injections

    // Storage
    List<Team> refTeams = new ArrayList<>();
    List<Team> filteredTeams = new ArrayList<>();

    // Fields
    private List<Team> vTeams = new ArrayList<>();

    public ManagerCreationBean(PersonService personService, LangBean langBean, UserAddBean userAddBean) {
        this.personService = personService;
        this.langBean = langBean;
        this.userAddBean = userAddBean;
    }

    /**
     * Reset the variables of the bean and load all the teams in the bean
     */
    public void init() {
        resetVariables();
        refTeams = personService.findAllTeams();
    }

    /**
     * Reset the variables of the bean
     */
    private void resetVariables() {
        refTeams = new ArrayList<>();
        filteredTeams = new ArrayList<>();
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
