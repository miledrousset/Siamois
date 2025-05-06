package fr.siamois.domain.services.person;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.Team;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.TeamAlreadyExistException;
import fr.siamois.infrastructure.database.repositories.person.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public List<Team> findTeamsOfInstitution(Person currentUser, Institution institution) {
        List<Team> teams = teamRepository.findTeamsByInstitution(institution);
        boolean defaultIsPresent = teams.stream().anyMatch(Team::isDefaultTeam);
        if (!defaultIsPresent) {
            Team defaultTeam = new Team();
            defaultTeam.setDefaultTeam(true);
            defaultTeam.setInstitution(institution);
            defaultTeam.setName("MEMBERS");
            defaultTeam.setMembers(Set.of(currentUser));
            defaultTeam.setDescription("Generated default team for all members of the organization");
            Team saved = teamRepository.save(defaultTeam);
            teams.add(saved);
        }
        return teams;
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
}
