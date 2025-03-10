package fr.siamois.domain.services.person;

import fr.siamois.domain.models.Team;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.auth.*;
import fr.siamois.domain.services.person.verifier.PersonDataVerifier;
import fr.siamois.infrastructure.repositories.auth.PersonRepository;
import fr.siamois.infrastructure.repositories.auth.TeamRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service to manage Person
 */
@Service
public class PersonService {

    private final TeamRepository teamRepository;
    private final PersonRepository personRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final List<PersonDataVerifier> verifiers;

    public PersonService(TeamRepository teamRepository,
                         PersonRepository personRepository,
                         BCryptPasswordEncoder passwordEncoder,
                         List<PersonDataVerifier> verifiers) {
        this.teamRepository = teamRepository;
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
        this.verifiers = verifiers;
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

    public Person createPerson(Person person) throws InvalidUsernameException, InvalidEmailException, UserAlreadyExistException, InvalidPasswordException, InvalidNameException {
        person.setId(-1L);

        checkPersonData(person);

        person.setPassword(passwordEncoder.encode(person.getPassword()));

        return personRepository.save(person);
    }

    private void checkPersonData(Person person) throws InvalidUsernameException, InvalidEmailException, UserAlreadyExistException, InvalidPasswordException, InvalidNameException {
        for (PersonDataVerifier verifier : verifiers) {
            verifier.verify(person);
        }
    }

    /**
     * Find all the person where name or lastname match the string. Case is ignored.
     *
     * @param nameOrLastname The string to look for in name or username
     * @return The Person list
     */
    public List<Person> findAllByNameLastnameContaining(String nameOrLastname) {
        return personRepository.findAllByNameOrLastname(nameOrLastname);
    }

    /**
     * Find a person by its ID
     * @param id The ID of the person
     * @return The person having the given ID
     */
    public Person findById(long id) {
        return personRepository.findById(id).orElse(null);
    }

    public Person updatePerson(Person person) throws UserAlreadyExistException, InvalidNameException, InvalidPasswordException, InvalidUsernameException, InvalidEmailException {
        checkPersonData(person);

        return personRepository.save(person);
    }

}
