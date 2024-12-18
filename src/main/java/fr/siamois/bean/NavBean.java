package fr.siamois.bean;

import fr.siamois.bean.converter.TeamConverter;
import fr.siamois.models.Team;
import fr.siamois.models.auth.Person;
import fr.siamois.services.TeamService;
import fr.siamois.services.TeamTopicSubscriber;
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

    private final List<TeamTopicSubscriber> subscribers;

    private List<Team> allTeams;

    private List<Team> teams;

    private Team selectedTeam;

    public NavBean(SessionSettings sessionSettings, TeamService teamService, TeamConverter converter) {
        this.sessionSettings = sessionSettings;
        this.teamService = teamService;
        this.converter = converter;
        subscribers = new ArrayList<>();
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
        Optional<Person> user = AuthenticatedUserUtils.getAuthenticatedUser();
        return user.map(person -> person.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase(roleName)))
                .orElse(false);
    }

    /**
     * Checks if the user is in any of the given roles
     * @param roles the roles to check
     * @return true if the user is in any of the roles, false otherwise
     */
    public boolean userIsAny(String... roles) {
        Optional<Person> user = AuthenticatedUserUtils.getAuthenticatedUser();
        return user.map(person -> person.getAuthorities()
                .stream()
                .anyMatch(a -> {
                    for (String role : roles) {
                        if (a.getAuthority().equalsIgnoreCase(role)) {
                            return true;
                        }
                    }
                    return false;
                }))
                .orElse(false);
    }

    /**
     * Notifies all subscribers that the team has changed
     */
    public void onTeamChange() {
        log.trace("Team changed to {}", selectedTeam.getName());
        sessionSettings.setSelectedTeam(selectedTeam);
        subscribers.forEach(TeamTopicSubscriber::onTeamChange);
    }

    /**
     * Add a subscriber to the list of subscribers
     * @param bean the subscriber to add
     */
    public void addSubscriber(TeamTopicSubscriber bean) {
        subscribers.add(bean);
    }
}
