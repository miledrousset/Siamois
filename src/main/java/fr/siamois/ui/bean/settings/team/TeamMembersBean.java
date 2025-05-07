package fr.siamois.ui.bean.settings.team;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.team.Team;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.team.TeamPerson;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.domain.services.person.TeamService;
import fr.siamois.domain.utils.DateUtils;
import fr.siamois.ui.bean.settings.SettingsDatatableBean;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Component
public class TeamMembersBean implements SettingsDatatableBean {

    private final InstitutionService institutionService;
    private final TeamService teamService;
    private Team team;

    private List<TeamPerson> members;
    private List<TeamPerson> filteredMembers;
    private String searchInput;

    public TeamMembersBean(InstitutionService institutionService, TeamService teamService) {
        this.institutionService = institutionService;
        this.teamService = teamService;
    }

    public void init(Team team) {
        this.team = team;
        this.members = teamService.findTeamPersonByTeam(team);
        this.filteredMembers = new ArrayList<>(members);
    }

    @Override
    public void add() {

    }

    @Override
    public void filter() {
        String input = getSearchInput();
        if (input.length() < 2) {
            filteredMembers = new ArrayList<>(members);
            return;
        }

        filteredMembers = new ArrayList<>();
        for (TeamPerson teamPerson : members) {
            Person member = teamPerson.getPerson();
            if (member.getUsername().toLowerCase().contains(getSearchInput().toLowerCase())) {
                filteredMembers.add(teamPerson);
            }
        }
    }

    private boolean userIsSuperAdmin(Person person) {
        return person.isSuperAdmin();
    }

    private boolean userIsManagerOf(Institution institution, Person person) {
        return person.getId().equals(institution.getManager().getId());
    }

    private boolean userIsOwnerOf(Institution institution, Person person) {
        return institutionService.isManagerOf(institution, person);
    }

    public String roleOf(Person member) {
        if (userIsSuperAdmin(member)) {
            return "Administrateur";
        } else if (userIsOwnerOf(team.getInstitution(), member)) {
            return "Propriétaire";
        } else if (userIsManagerOf(team.getInstitution(), member)) {
            return "Responsable";
        } else {
            return "Utilisateur";
        }
    }

    public String formatDate(OffsetDateTime offsetDateTime) {
        return DateUtils.formatOffsetDateTime(offsetDateTime);
    }

}
