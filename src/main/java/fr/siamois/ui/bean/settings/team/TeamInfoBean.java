package fr.siamois.ui.bean.settings.team;

import fr.siamois.domain.models.institution.Team;
import fr.siamois.domain.models.exceptions.TeamAlreadyExistException;
import fr.siamois.domain.services.person.TeamService;
import fr.siamois.ui.bean.LangBean;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Serializable;

import static fr.siamois.domain.utils.MessageUtils.*;

@Slf4j
@Setter
@Getter
@Component
public class TeamInfoBean implements Serializable {

    private final LangBean langBean;
    private final transient TeamService teamService;
    private Team team;

    private String teamName;
    private String teamDescription;

    public TeamInfoBean(LangBean langBean, TeamService teamService) {
        this.langBean = langBean;
        this.teamService = teamService;
    }

    private void reset() {
        teamName = null;
        teamDescription = null;
        team = null;
    }

    public void init(Team team) {
        reset();
        this.team = team;
        this.teamName = team.getName();

        if (team.isDefaultTeam()) {
            teamName = nameOf(team);
        }

        this.teamDescription = team.getDescription();
    }

    public void save() {

        boolean changed = false;
        if (!team.isDefaultTeam() && !teamName.equalsIgnoreCase(team.getName())) {
            changed = true;
            team.setName(teamName);
        }

        if (!teamDescription.equalsIgnoreCase(team.getDescription())) {
            changed = true;
            team.setDescription(teamDescription);
        }

        if (changed) {
            try {
                team = teamService.update(team);
                displayInfoMessage(langBean, "groupSettings.info.updated");
            } catch (TeamAlreadyExistException e) {
                displayErrorMessage(langBean, "common.entity.team.alreadyExist",
                        teamName,
                        team.getInstitution().getName());
                teamName = team.getName();
            }
        } else {
            displayWarnMessage(langBean, "common.error.unchanged");
        }
    }

    public String nameOf(Team team) {
        if (team.isDefaultTeam()) {
            return langBean.msg("common.entity.members");
        }
        return team.getName();
    }

}
