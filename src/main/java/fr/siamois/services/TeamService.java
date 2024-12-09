package fr.siamois.services;

import fr.siamois.infrastructure.repositories.auth.PersonRepository;
import fr.siamois.infrastructure.repositories.auth.SystemRoleRepository;
import fr.siamois.infrastructure.repositories.auth.TeamRepository;
import fr.siamois.models.Team;
import fr.siamois.models.auth.Person;
import fr.siamois.models.auth.SystemRole;
import fr.siamois.models.exceptions.TeamAlreadyExistException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeamService {

    private final SystemRoleRepository systemRoleRepository;
    private final PersonRepository personRepository;
    private final TeamRepository teamRepository;

    public TeamService(SystemRoleRepository systemRoleRepository, PersonRepository personRepository, TeamRepository teamRepository) {
        this.systemRoleRepository = systemRoleRepository;
        this.personRepository = personRepository;
        this.teamRepository = teamRepository;
    }

    public List<Person> findAllManagers() {
        SystemRole managerRole = systemRoleRepository.findSystemRoleByRoleNameIgnoreCase("TEAM_MANAGER").orElseThrow(() -> new IllegalStateException("TEAM_MANAGER role not found and should be created at startup."));
        return personRepository.findPersonsWithSystemRole(managerRole.getId());
    }

    public void createTeam(String teamName, String description, Person teamManager) throws TeamAlreadyExistException {
        Optional<Team> existingTeam = teamRepository.findTeamByNameIgnoreCase(teamName);
        if (existingTeam.isPresent()) throw new TeamAlreadyExistException("Team with name " + teamName + " already exists.");

        Team team = new Team();
        team.setName(teamName);
        team.setDescription(description);

        team = teamRepository.save(team);

        personRepository.addManagerToTeam(teamManager.getId(), team.getId());

    }
}
