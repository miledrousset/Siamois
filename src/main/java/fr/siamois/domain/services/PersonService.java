package fr.siamois.domain.services;

import fr.siamois.domain.models.Team;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.UserAlreadyExist;
import fr.siamois.domain.models.exceptions.auth.InvalidEmail;
import fr.siamois.domain.models.exceptions.auth.InvalidPassword;
import fr.siamois.domain.models.exceptions.auth.InvalidUsername;
import fr.siamois.infrastructure.repositories.auth.PersonRepository;
import fr.siamois.infrastructure.repositories.auth.TeamRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service to manage Person
 */
@Service
public class PersonService {

    private final TeamRepository teamRepository;
    private final PersonRepository personRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public PersonService(TeamRepository teamRepository,
                         PersonRepository personRepository,
                         BCryptPasswordEncoder passwordEncoder) {
        this.teamRepository = teamRepository;
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Find all the teams in the database
     * @return The list of teams in the database
     */
    public List<Team> findAllTeams() {
        List<Team> result = new ArrayList<>();
        for (Team t : teamRepository.findAll()) result.add(t);
        return result;
    }

    /**
     * Create a person with the given username, email and password.
     * The username must contain only letters, numbers and dots.
     * The email must be a valid email.
     * The password must be at least 8 characters long.
     * @param username The username of the person
     * @param email The email of the person
     * @param password The plain password of the person
     * @return The created person in the database
     * @throws InvalidUsername If the username is invalid
     * @throws UserAlreadyExist If the username already exists
     * @throws InvalidEmail If the email is invalid
     * @throws InvalidPassword If the password is invalid
     */
    public Person createPerson(String username, String email, String password) throws InvalidUsername, UserAlreadyExist, InvalidEmail, InvalidPassword {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9.]+$");
        Matcher matcher = pattern.matcher(username);

        if (StringUtils.isBlank(username)) throw new InvalidUsername("Username cannot be empty.");
        if (!matcher.find()) throw new InvalidUsername("Username must contain only letters, numbers and dots.");

        Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        Matcher emailMatcher = emailPattern.matcher(email);

        if (StringUtils.isBlank(email)) throw new InvalidEmail("Email cannot be empty.");
        if (!emailMatcher.find()) throw new InvalidEmail("Email is not valid.");

        if (StringUtils.isBlank(password)) throw new InvalidPassword("Password cannot be empty.");
        if (password.length() < 8) throw new InvalidPassword("Password must be at least 8 characters long.");

        Optional<Person> optPerson = personRepository.findByUsernameIgnoreCase(username);
        if (optPerson.isPresent()) throw new UserAlreadyExist("Username already exists.");

        Person person = new Person();
        person.setUsername(username);
        person.setMail(email);
        person.setPassword(passwordEncoder.encode(password));

        return personRepository.save(person);
    }

    /**
     * Find all the person where name or lastname match the string. Case is ignored.
     *
     * @param nameOrLastname The string to look for in name or username
     * @return The Person list
     */
    public List<Person> findAllByNameLastnameContaining(String nameOrLastname) {
        return personRepository.findAllByNameIsContainingIgnoreCaseOrLastnameIsContainingIgnoreCase(nameOrLastname, nameOrLastname);
    }

    /**
     * Find a person by its ID
     * @param id The ID of the person
     * @return The person having the given ID
     */
    public Person findById(long id) {
        return personRepository.findById(id).orElse(null);
    }

}
