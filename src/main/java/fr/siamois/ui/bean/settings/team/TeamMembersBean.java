package fr.siamois.ui.bean.settings.team;

import fr.siamois.domain.models.auth.pending.PendingInstitutionInvite;
import fr.siamois.domain.models.auth.pending.PendingPerson;
import fr.siamois.domain.models.team.Team;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.team.TeamPerson;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.services.auth.PendingPersonService;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.domain.services.person.TeamService;
import fr.siamois.domain.utils.DateUtils;
import fr.siamois.ui.bean.LabelBean;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.dialog.institution.UserDialogBean;
import fr.siamois.ui.bean.settings.SettingsDatatableBean;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static fr.siamois.domain.utils.MessageUtils.displayInfoMessage;

@Slf4j
@Setter
@Getter
@Component
public class TeamMembersBean implements SettingsDatatableBean {

    private final transient InstitutionService institutionService;
    private final transient TeamService teamService;
    private final UserDialogBean userDialogBean;
    private final transient PersonService personService;
    private final LabelBean labelBean;
    private final transient PendingPersonService pendingPersonService;
    private final SessionSettingsBean sessionSettingsBean;
    private final LangBean langBean;
    private Team team;

    private transient List<TeamPerson> members;
    private transient List<TeamPerson> filteredMembers;
    private String searchInput;

    public TeamMembersBean(InstitutionService institutionService, TeamService teamService, UserDialogBean userDialogBean, PersonService personService, LabelBean labelBean, PendingPersonService pendingPersonService, SessionSettingsBean sessionSettingsBean, LangBean langBean) {
        this.institutionService = institutionService;
        this.teamService = teamService;
        this.userDialogBean = userDialogBean;
        this.personService = personService;
        this.labelBean = labelBean;
        this.pendingPersonService = pendingPersonService;
        this.sessionSettingsBean = sessionSettingsBean;
        this.langBean = langBean;
    }

    public void init(Team team) {
        this.team = team;
        this.members = teamService.findTeamPersonByTeam(team);
        this.filteredMembers = new ArrayList<>(members);
    }

    @Override
    public void add() {
        userDialogBean.init("Ajouter un membre", "Ajouter", team.getInstitution(), true, this::save);
        PrimeFaces.current().ajax().update("newMemberDialog");
        PrimeFaces.current().executeScript("PF('newMemberDialog').show();");
    }

    private void save() {
        Optional<Person> existing = personService.findByEmail(userDialogBean.getUserEmail());
        Concept role = userDialogBean.getRole();
        if (team.isDefaultTeam()) {
            role = null;
        }

        if (existing.isPresent()) {
            Person person = existing.get();
            teamService.addPersonToTeam(person, team, role);
            displayInfoMessage(langBean, "groupManagement.join.success", person.getEmail(), team.getName());
        } else {
            PendingPerson pendingPerson = pendingPersonService.createOrGetPendingPerson(userDialogBean.getUserEmail());
            boolean mailSent = pendingPersonService.pendingInstitutionInviteIsSent(pendingPerson,
                    team.getInstitution(),
                    sessionSettingsBean.getLanguageCode());

            PendingInstitutionInvite pendingInstit = pendingPersonService.findByInstitutionAndPendingPerson(team.getInstitution(), pendingPerson)
                    .orElseThrow(() -> new IllegalStateException("Pending institution invite should exist"));
            pendingPersonService.addTeamToInvitation(pendingInstit, team, role);

            if (mailSent) {
                displayInfoMessage(langBean, "organisationSettings.action.sendInvite", pendingPerson.getEmail());
            } else {
                displayInfoMessage(langBean, "groupManagement.join.success", pendingPerson.getEmail(), team.getName());
            }
        }
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

    public String roleOf(TeamPerson member) {
        if (userIsSuperAdmin(member.getPerson())) {
            if (member.getRoleInTeam() == null ){
                return "Administrateur";
            } else {
                return String.format("%s (Administrateur)", labelBean.findLabelOf(member.getRoleInTeam()));
            }
        } else {
            return labelBean.findLabelOf(member.getRoleInTeam());
        }
    }

    public String formatDate(OffsetDateTime offsetDateTime) {
        return DateUtils.formatOffsetDateTime(offsetDateTime);
    }

}
