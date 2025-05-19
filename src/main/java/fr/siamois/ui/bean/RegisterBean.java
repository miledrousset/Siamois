package fr.siamois.ui.bean;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.auth.pending.PendingInstitutionInvite;
import fr.siamois.domain.models.auth.pending.PendingPerson;
import fr.siamois.domain.models.auth.pending.PendingTeamInvite;
import fr.siamois.domain.models.exceptions.auth.InvalidUserInformationException;
import fr.siamois.domain.models.exceptions.auth.UserAlreadyExistException;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.institution.Team;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.services.auth.PendingPersonService;
import fr.siamois.domain.services.person.PersonService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@SessionScoped
@Getter
@Setter
public class RegisterBean {

    private final PersonService personService;
    private final InstitutionService institutionService;
    private final LangBean langBean;
    private final RedirectBean redirectBean;
    private final PendingPersonService pendingPersonService;
    private String email;
    private String password;
    private String confirmPassword;
    private Institution institution;
    private String firstName;
    private String lastName;
    private String username;

    private Map<Institution, Set<Team>> institutionTeams = new HashMap<>();

    public RegisterBean(PersonService personService,
                        InstitutionService institutionService,
                        LangBean langBean,
                        RedirectBean redirectBean, PendingPersonService pendingPersonService) {
        this.personService = personService;
        this.institutionService = institutionService;
        this.langBean = langBean;
        this.redirectBean = redirectBean;
        this.pendingPersonService = pendingPersonService;
    }

    public void reset() {
        email = null;
        password = null;
        confirmPassword = null;
        institution = null;
        firstName = null;
        lastName = null;
        username = null;
    }

    public void init(PendingPerson pendingPerson) {
        reset();
        this.email = pendingPerson.getEmail();
        Set<PendingInstitutionInvite> institutions = pendingPersonService.findInstitutionsByPendingPerson(pendingPerson);
        for (PendingInstitutionInvite invite : institutions) {
            institutionTeams.putIfAbsent(invite.getInstitution(), new HashSet<>());
            Set<PendingTeamInvite> teamInvites = pendingPersonService.findTeamsByPendingInstitutionInvite(invite);
            teamInvites.forEach(teamInvite ->
                    institutionTeams.get(teamInvite.getPendingInstitutionInvite().getInstitution()).add(teamInvite.getTeam()));

        }
    }

    public void register() {

        if (email == null || password == null || confirmPassword == null) {
            log.trace("Email and password are not set");
            return;
        }

        if (!password.equals(confirmPassword)) {
            log.trace("Password and confirm password are not the same");
            return;
        }

        Person person = new Person();
        person.setEmail(email);
        person.setName(firstName);
        person.setLastname(lastName);
        person.setPassword(password);
        person.setUsername(username);

        try {
            personService.createPerson(person);
            log.trace("Person created");

            redirectBean.redirectTo("/login");

        } catch (InvalidUserInformationException e) {
            log.trace("Person could not be created");
        } catch (UserAlreadyExistException e) {
            log.trace("User already exists");
        }

    }

    public String getTeamsName(Institution institution) {
        Set<String> teamsName = institutionTeams.getOrDefault(institution, Set.of())
                .stream()
                .filter(t -> !t.isDefaultTeam())
                .map(Team::getName)
                .collect(Collectors.toSet());
        return "[" + String.join(", ", teamsName) + "]";
    }

}
