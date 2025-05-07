package fr.siamois.ui.bean.dialog;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.team.Team;
import fr.siamois.domain.models.exceptions.TeamAlreadyExistException;
import fr.siamois.domain.services.person.TeamService;
import fr.siamois.domain.utils.MessageUtils;
import fr.siamois.ui.bean.ActionFromBean;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Setter
@Getter
@Component
public class NewTeamDialogBean implements Serializable {

    private final transient TeamService teamService;
    private final LangBean langBean;
    private final SessionSettingsBean sessionSettingsBean;
    private Institution institution;
    private String name;
    private String description;

    private transient ActionFromBean actionFromBean;

    public NewTeamDialogBean(TeamService teamService, LangBean langBean, SessionSettingsBean sessionSettingsBean) {
        this.teamService = teamService;
        this.langBean = langBean;
        this.sessionSettingsBean = sessionSettingsBean;
    }

    public void reset() {
        this.name = null;
        this.description = null;
        this.actionFromBean = null;
        this.institution = null;
    }

    public void init(Institution institution, ActionFromBean actionFromBean) {
        reset();
        this.institution = institution;
        this.actionFromBean = actionFromBean;
    }

    public void exit() {
        PrimeFaces.current().executeScript("PF('newTeamDialog').close();");
    }

    public Team createTeam() {
        Team team = new Team();
        team.setName(name);
        team.setDescription(description);
        team.setInstitution(institution);
        team.setDefaultTeam(false);
        try {
            Team saved = teamService.create(team);
            teamService.addPersonToTeam(sessionSettingsBean.getAuthenticatedUser(), team);
            MessageUtils.displayInfoMessage(langBean, "groupManagement.success", saved.getName());
            return saved;
        } catch (TeamAlreadyExistException e) {
            MessageUtils.displayErrorMessage(langBean, "common.entity.team.alreadyExist", name, institution.getName());
            return null;
        }
    }

}
