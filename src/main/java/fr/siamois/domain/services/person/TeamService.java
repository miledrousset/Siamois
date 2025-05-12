package fr.siamois.domain.services.person;

import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.institution.Team;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.TeamAlreadyExistException;
import fr.siamois.domain.models.institution.TeamPerson;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.infrastructure.database.repositories.person.TeamPersonRepository;
import fr.siamois.infrastructure.database.repositories.person.TeamRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamPersonRepository teamPersonRepository;

    public TeamService(TeamRepository teamRepository, TeamPersonRepository teamPersonRepository) {
        this.teamRepository = teamRepository;
        this.teamPersonRepository = teamPersonRepository;
    }

    public void addPersonToTeamIfNotAdded(Person person, Team team, Concept role) {
        Optional<TeamPerson> opt = teamPersonRepository.findByPersonAndTeam(person, team);
        TeamPerson teamPerson;
        if (opt.isEmpty()) {
            teamPerson = new TeamPerson(team, person, role);
        } else {
            teamPerson = opt.get();
            teamPerson.setRoleInTeam(role);
        }
        teamPersonRepository.save(teamPerson);
    }

    public void addPersonToInstitutionIfNotExist(Person person, Institution institution) {
        Optional<TeamPerson> optTp = teamPersonRepository.findDefaultOfInstitution(institution.getId());
        Team team;
        if (optTp.isEmpty()) {
            team = createDefaultTeamOf(institution);
        } else {
            team = optTp.get().getTeam();
        }
        addPersonToTeamIfNotAdded(person, team, null);
    }

    public List<Team> findTeamsOfInstitution(Person currentUser, Institution institution) {
        List<Team> teams = teamRepository.findTeamsByInstitution(institution);
        boolean defaultIsPresent = teams.stream().anyMatch(Team::isDefaultTeam);
        if (!defaultIsPresent) {
            Team savedDefaultTeam = createDefaultTeamOf(institution);
            addPersonToTeamIfNotAdded(currentUser, savedDefaultTeam, null);
            teams.add(savedDefaultTeam);
        }
        return teams;
    }


    private Team createDefaultTeamOf(Institution institution) {
        Team defaultTeam = new Team();
        defaultTeam.setDefaultTeam(true);
        defaultTeam.setInstitution(institution);
        defaultTeam.setName("MEMBERS");
        defaultTeam.setDescription("Generated default team for all members of the organization");
        return teamRepository.save(defaultTeam);
    }

    public long numberOfMembersInTeam(Team team) {
        return teamRepository.countMembersOfTeam(team.getId());
    }

    public Team create(Team team) throws TeamAlreadyExistException {
        if (nameAlreadyExists(team.getInstitution(), team.getName())) {
            throw new TeamAlreadyExistException(team.getName(), team.getInstitution().getName());
        }
        return teamRepository.save(team);
    }

    private boolean nameAlreadyExists(Institution institution, String name) {
        return teamRepository.findTeamByNameInInstitution(institution.getId(), name).isPresent();
    }

    public Team update(Team team) throws TeamAlreadyExistException {
        Optional<Team> optTeam = teamRepository.findById(team.getId());
        if (optTeam.isPresent()) {
            Team oldTeam = optTeam.get();
            if (nameHasChanged(team, oldTeam) && nameAlreadyExists(team.getInstitution(), team.getName())) {
                throw new TeamAlreadyExistException(team.getName(), team.getInstitution().getName());
            }
        }
        return teamRepository.save(team);
    }

    private static boolean nameHasChanged(Team newTeam, Team oldTeam) {
        return !oldTeam.getName().equalsIgnoreCase(newTeam.getName());
    }

    public List<TeamPerson> findMembersOf(Institution institution) {
        return teamPersonRepository.findAllOfInstitution(institution.getId());
    }

    public List<TeamPerson> findTeamPersonByTeam(Team team) {
        if (team.isDefaultTeam()) {
            return findMembersOf(team.getInstitution());
        }
        return teamPersonRepository.findByTeam(team);
    }

    public OffsetDateTime findEarliestAddDateInInstitution(Institution institution, Person person) {
        return teamPersonRepository.findEarliestAddDateInInstitution(institution.getId(), person.getId());
    }
}
