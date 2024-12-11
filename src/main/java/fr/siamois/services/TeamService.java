package fr.siamois.services;

import fr.siamois.infrastructure.api.dto.ConceptFieldDTO;
import fr.siamois.infrastructure.repositories.auth.PersonRepository;
import fr.siamois.infrastructure.repositories.auth.SystemRoleRepository;
import fr.siamois.infrastructure.repositories.auth.TeamRepository;
import fr.siamois.infrastructure.repositories.vocabulary.ConceptRepository;
import fr.siamois.models.Team;
import fr.siamois.models.auth.Person;
import fr.siamois.models.auth.SystemRole;
import fr.siamois.models.exceptions.FailedTeamSaveException;
import fr.siamois.models.exceptions.TeamAlreadyExistException;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.services.vocabulary.FieldConfigurationService;
import fr.siamois.services.vocabulary.FieldService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TeamService {

    private final SystemRoleRepository systemRoleRepository;
    private final PersonRepository personRepository;
    private final TeamRepository teamRepository;
    private final ConceptRepository conceptRepository;
    private final FieldService fieldService;
    private final FieldConfigurationService fieldConfigurationService;

    public TeamService(SystemRoleRepository systemRoleRepository, PersonRepository personRepository, TeamRepository teamRepository, ConceptRepository conceptRepository, FieldService fieldService, FieldConfigurationService fieldConfigurationService) {
        this.systemRoleRepository = systemRoleRepository;
        this.personRepository = personRepository;
        this.teamRepository = teamRepository;
        this.conceptRepository = conceptRepository;
        this.fieldService = fieldService;
        this.fieldConfigurationService = fieldConfigurationService;
    }

    public List<Person> findAllManagers() {
        SystemRole managerRole = systemRoleRepository.findSystemRoleByRoleNameIgnoreCase("TEAM_MANAGER").orElseThrow(() -> new IllegalStateException("TEAM_MANAGER role not found and should be created at startup."));
        return personRepository.findPersonsWithSystemRole(managerRole.getId());
    }

    public void createTeam(String teamName, String description, Person teamManager) throws TeamAlreadyExistException, FailedTeamSaveException {
        Optional<Team> existingTeam = teamRepository.findTeamByNameIgnoreCase(teamName);
        if (existingTeam.isPresent()) throw new TeamAlreadyExistException("Team with name " + teamName + " already exists.");

        Team team = new Team();
        team.setName(teamName);
        team.setDescription(description);

        team = teamRepository.save(team);

        int affected = personRepository.addManagerToTeam(teamManager.getId(), team.getId());
        if (affected == 0) throw new FailedTeamSaveException("Failed to add person to any team");

    }

    public void addUserToTeam(Person person, Team team, Concept role) throws FailedTeamSaveException {
        int affectedRows = personRepository.addUserToTeam(person.getId(), team.getId(), role.getId());
        if (affectedRows == 0) throw new FailedTeamSaveException("Failed to add user to team " + team.getName());
    }

    public List<Person> findTeamMembers(Team team) {
        return personRepository.findTeamMembers(team.getId());
    }

    public List<Team> findTeamsOfPerson(Person authenticatedUser) {
        log.trace("Finding teams of person {}", authenticatedUser);
        return teamRepository.findTeamsOfPerson(authenticatedUser.getId());
    }

    public List<Team> findAllTeams() {
        List<Team> teams = new ArrayList<>();

        for (Team t : teamRepository.findAll()) {
            teams.add(t);
        }

        return teams;
    }
}
