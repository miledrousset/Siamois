package fr.siamois.bean;

import fr.siamois.bean.converter.TeamConverter;
import fr.siamois.models.Team;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.NoTeamSelectedException;
import fr.siamois.services.TeamService;
import fr.siamois.services.Subscriber;
import fr.siamois.utils.AuthenticatedUserUtils;
import jakarta.faces.context.ExternalContext;
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


/**
 * Bean to manage the navigation bar of the application. Allows the user to select a team.
 *
 * @author Julien Linget
 */
@Slf4j
@Component
@Getter
@Setter
@SessionScoped
public class NavBean implements Serializable {

    private final SessionSettings sessionSettings;
    private final TeamService teamService;
    private final TeamConverter converter;
    private final ObserverBean observerBean;
    private final TeamConverter teamConverter;

    private List<Team> allTeams;

    private List<Team> teams;

    private Team selectedTeam;

    public NavBean(SessionSettings sessionSettings, TeamService teamService, TeamConverter converter, ObserverBean observerBean, TeamConverter teamConverter) {
        this.sessionSettings = sessionSettings;
        this.teamService = teamService;
        this.converter = converter;
        this.observerBean = observerBean;
        this.teamConverter = teamConverter;
        log.trace("Nav bean constructor called");
    }


    /**
     * Builds the logout path with the context path
     * @return the logout path
     */
    public String logoutPath() {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        String contextPath = externalContext.getRequestContextPath();
        return contextPath + "/logout";
    }

    /**
     * Checks if the user is in the given role
     * @param roleName the role to check
     * @return true if the user is in the role, false otherwise
     */
    public boolean userIs(String roleName) {
        Optional<Person> optUser = AuthenticatedUserUtils.getAuthenticatedUser();
        return optUser.map(person -> person.hasRole(roleName)).orElse(false);
    }

    /**
     * Checks if the user is in any of the given roles
     * @param roles the roles to check
     * @return true if the user is in any of the roles, false otherwise
     */
    public boolean userIsAny(String... roles) {
        Optional<Person> optUser = AuthenticatedUserUtils.getAuthenticatedUser();
        if (optUser.isEmpty()) return false;

        Person user = optUser.get();
        for (String role : roles) {
            if (user.hasRole(role)) return true;
        }
        return false;
    }

    public void changeSelectedTeam() {
        try {
            Team oldTeam = sessionSettings.getSelectedTeam();
            sessionSettings.setSelectedTeam(selectedTeam);
            observerBean.notify("teamChange");
            log.trace("Team changed from {} to {}", oldTeam.toString(), selectedTeam.toString());
        } catch (NoTeamSelectedException e) {
            log.error("No team selected", e);
        }
    }
}
