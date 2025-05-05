package fr.siamois.domain.services.person;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.Team;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.TeamAlreadyExistException;
import fr.siamois.infrastructure.database.repositories.person.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.List;
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
        if (teamRepository.findTeamByNameInInstitution(team.getInstitution().getId(), team.getName()).isPresent()) {
            throw new TeamAlreadyExistException(team.getName(), team.getInstitution().getName());
        }
        return teamRepository.save(team);
    }
}
