package fr.siamois.services;

import fr.siamois.infrastructure.repositories.auth.PersonRepository;
import fr.siamois.infrastructure.repositories.auth.SystemRoleRepository;
import fr.siamois.infrastructure.repositories.auth.TeamRepository;
import fr.siamois.models.Team;
import fr.siamois.models.auth.Person;
import fr.siamois.models.auth.SystemRole;
import fr.siamois.models.exceptions.UserAlreadyExist;
import fr.siamois.models.exceptions.field.InvalidUserInformation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PersonService {

    private final TeamRepository teamRepository;
    private final PersonRepository personRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SystemRoleRepository systemRoleRepository;

    public PersonService(TeamRepository teamRepository, PersonRepository personRepository, BCryptPasswordEncoder passwordEncoder, SystemRoleRepository systemRoleRepository) {
        this.teamRepository = teamRepository;
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
        this.systemRoleRepository = systemRoleRepository;
    }

    public List<Team> findAllTeams() {
        List<Team> result = new ArrayList<>();
        for (Team t : teamRepository.findAll()) result.add(t);
        return result;
    }

    public Person createPerson(String username, String email, String password) throws InvalidUserInformation, UserAlreadyExist {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9.]+$");
        Matcher matcher = pattern.matcher(username);

        if (StringUtils.isBlank(username)) throw new InvalidUserInformation("Username cannot be empty.");
        if (!matcher.find()) throw new InvalidUserInformation("Username must contain only letters, numbers and dots.");

        Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        Matcher emailMatcher = emailPattern.matcher(email);

        if (StringUtils.isBlank(email)) throw new InvalidUserInformation("Email cannot be empty.");
        if (!emailMatcher.find()) throw new InvalidUserInformation("Email is not valid.");

        if (StringUtils.isBlank(password)) throw new InvalidUserInformation("Password cannot be empty.");
        if (password.length() < 8) throw new InvalidUserInformation("Password must be at least 8 characters long.");

        Optional<Person> optPerson = personRepository.findPersonByUsername(username);
        if (optPerson.isPresent()) throw new UserAlreadyExist("Username already exists.");

        Person person = new Person();
        person.setUsername(username);
        person.setMail(email);
        person.setPassword(passwordEncoder.encode(password));

        return personRepository.save(person);
    }

    public Person addPersonToTeamManagers(Person person) {
        SystemRole role = systemRoleRepository.findSystemRoleByRoleNameIgnoreCase("TEAM_MANAGER").orElseThrow(() -> new IllegalStateException("Team manager role should be created."));
        person.getRoles().add(role);
        return personRepository.save(person);
    }

    public void addPersonToTeam(Person person, Team... teams) throws InvalidUserInformation {
        int affected = 0;

        for (Team t : teams) {
            int rowAffected = personRepository.addManagerToTeam(person.getId(), t.getId());
            if (rowAffected == 0) throw new InvalidUserInformation("Failed to add person to team " + t.getName());
            affected += rowAffected;
        }

        if (affected == 0) throw new InvalidUserInformation("Failed to add person to any team");
    }

}
