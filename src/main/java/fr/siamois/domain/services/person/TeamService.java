package fr.siamois.domain.services.person;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.TeamAlreadyExistException;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.institution.Team;
import fr.siamois.domain.models.institution.TeamPerson;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.infrastructure.database.repositories.person.TeamPersonRepository;
import fr.siamois.infrastructure.database.repositories.person.TeamRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;

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
        Optional<Team> opTeam = teamRepository.findDefaultOf(institution.getId());
        Team team = opTeam.orElseGet(() -> createDefaultTeamOf(institution));
        addPersonToTeamIfNotAdded(person, team, null);
    }

    public Set<Team> findTeamsOfInstitution(Person currentUser, Institution institution) {
        Set<Team> teams = new HashSet<>(teamRepository.findTeamsByInstitution(institution));
        boolean defaultIsPresent = teams.stream().anyMatch(Team::isDefaultTeam);
        if (!defaultIsPresent) {
            Team savedDefaultTeam = createDefaultTeamOf(institution);
            addPersonToTeamIfNotAdded(currentUser, savedDefaultTeam, null);
            teams.add(savedDefaultTeam);
        }
        return teams;
    }

    public Team createOrGetDefaultOf(Institution institution) {
        return teamRepository.findDefaultOf(institution.getId()).orElseGet(() -> createDefaultTeamOf(institution));
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

    public Set<TeamPerson> findMembersOf(Institution institution) {
        return teamPersonRepository.findAllOfInstitution(institution.getId());
    }

    public List<TeamPerson> findTeamPersonByTeam(Team team) {
        List<TeamPerson> persons;
        if (team.isDefaultTeam()) {
            persons = List.copyOf(findMembersOf(team.getInstitution()));
        } else {
            persons = teamPersonRepository.findByTeam(team);
        }

        Map<Person, List<TeamPerson>> groupedByPerson = new HashMap<>();
        for (TeamPerson teamPerson : persons) {
            groupedByPerson.computeIfAbsent(teamPerson.getPerson(), k -> new ArrayList<>()).add(teamPerson);
        }

        List<TeamPerson> result = new ArrayList<>();
        for (List<TeamPerson> teamPersons : groupedByPerson.values()) {
            if (teamPersons.size() > 1) {
                result.add(teamPersons.stream()
                        .filter(tp -> tp.getRoleInTeam() != null)
                        .findFirst()
                        .orElse(teamPersons.get(0)));
            } else {
                result.add(teamPersons.get(0));
            }
        }

        return result;

    }

    public OffsetDateTime findEarliestAddDateInInstitution(Institution institution, Person person) {
        return teamPersonRepository.findEarliestAddDateInInstitution(institution.getId(), person.getId());
    }

    public SortedSet<Team> findTeamsOfPersonInInstitution(Person user, Institution institution) {
        Set<TeamPerson> teams = teamPersonRepository.findAllOfInstitution(institution.getId());
        SortedSet<Team> teamSet = new TreeSet<>();
        for (TeamPerson teamPerson : teams) {
            if (teamPerson.getPerson().getId().equals(user.getId())) {
                teamSet.add(teamPerson.getTeam());
            }
        }
        return teamSet;
    }
}
