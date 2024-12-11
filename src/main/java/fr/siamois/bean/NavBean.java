package fr.siamois.bean;

import fr.siamois.bean.converter.TeamConverter;
import fr.siamois.models.Team;
import fr.siamois.models.auth.Person;
import fr.siamois.services.TeamTopicSubscriber;
import fr.siamois.services.TeamService;
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


    public String logoutPath() {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        String contextPath = externalContext.getRequestContextPath();
        return contextPath + "/logout";
    }

    public boolean userIs(String roleName) {
        Optional<Person> user = AuthenticatedUserUtils.getAuthenticatedUser();
        return user.map(person -> person.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase(roleName)))
                .orElse(false);
    }

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

    public void onTeamChange() {
        log.trace("Team changed to {}", selectedTeam.getName());
        sessionSettings.setSelectedTeam(selectedTeam);
        for (TeamTopicSubscriber subscriber : subscribers) {
            subscriber.onTeamChange();
        }
    }

    public void addSubscriber(TeamTopicSubscriber bean) {
        subscribers.add(bean);
    }
}
