package fr.siamois.ui.bean.settings.team;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.institution.Team;
import fr.siamois.domain.services.person.TeamService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.dialog.NewTeamDialogBean;
import fr.siamois.ui.bean.settings.SettingsDatatableBean;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@Getter
@Setter
public class TeamListBean implements SettingsDatatableBean {

    private final transient TeamService teamService;
    private final SessionSettingsBean sessionSettingsBean;
    private final LangBean langBean;
    private final NewTeamDialogBean newTeamDialogBean;
    private final TeamDetailsBean teamDetailsBean;
    private Institution institution;

    private Set<Team> teams;
    private List<Team> filteredTeams;

    private String searchInput;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TeamListBean(TeamService teamService, SessionSettingsBean sessionSettingsBean, LangBean langBean, NewTeamDialogBean newTeamDialogBean, TeamDetailsBean teamDetailsBean) {
        this.teamService = teamService;
        this.sessionSettingsBean = sessionSettingsBean;
        this.langBean = langBean;
        this.newTeamDialogBean = newTeamDialogBean;
        this.teamDetailsBean = teamDetailsBean;
    }

    public void init(Institution institution) {
        Person person = sessionSettingsBean.getAuthenticatedUser();
        this.institution = institution;
        this.teams = teamService.findTeamsOfInstitution(person, institution);
        this.filteredTeams = new ArrayList<>(teams);
    }

    @Override
    public void add() {
        newTeamDialogBean.init(institution, this::addTeam);
        PrimeFaces.current().executeScript("PF('newTeamDialog').show();");
    }

    public void addTeam() {
        Team team = newTeamDialogBean.createTeam();
        if (team != null) {
            teams.add(team);
            filteredTeams.add(team);
            newTeamDialogBean.exit();
        }
    }

    @Override
    public void filter() {
        filteredTeams.clear();
        if (searchInput == null || searchInput.isEmpty()) {
            filteredTeams.addAll(teams);
        } else {
            for (Team team : teams) {
                if (team.getName().toLowerCase().contains(searchInput.toLowerCase())) {
                    filteredTeams.add(team);
                }
            }
        }
    }

    public String formatCreationDate(Team team) {
        return team.getCreationDate() == null ? "" : DATE_TIME_FORMATTER.format(team.getCreationDate());
    }

    public long memberCount(Team team) {
        return teamService.numberOfMembersInTeam(team);
    }

    public String redirectToTeam(Team team) {
        teamDetailsBean.init(team);
        return "/pages/settings/team/teamDetailsSettings.xhtml?faces-redirect=true";
    }

    public String nameOf(Team team) {
        if (team.isDefaultTeam()) {
            return langBean.msg("common.entity.members");
        }
        return team.getName();
    }

    public String goToTeamList() {
        return "/pages/settings/team/teamList.xhtml?faces-redirect=true";
    }

}
