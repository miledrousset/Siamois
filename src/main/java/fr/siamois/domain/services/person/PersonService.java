package fr.siamois.domain.services.person;

import fr.siamois.domain.models.Team;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.auth.*;
import fr.siamois.domain.models.settings.PersonSettings;
import fr.siamois.domain.services.person.verifier.PasswordVerifier;
import fr.siamois.domain.services.person.verifier.PersonDataVerifier;
import fr.siamois.infrastructure.database.repositories.auth.PersonRepository;
import fr.siamois.infrastructure.database.repositories.auth.TeamRepository;
import fr.siamois.infrastructure.database.repositories.settings.PersonSettingsRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service to manage Person
 */
@Service
public class PersonService {

    private final TeamRepository teamRepository;
    private final PersonRepository personRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final List<PersonDataVerifier> verifiers;
    private final PersonSettingsRepository personSettingsRepository;

    public PersonService(TeamRepository teamRepository,
                         PersonRepository personRepository,
                         BCryptPasswordEncoder passwordEncoder,
                         List<PersonDataVerifier> verifiers,
                         PersonSettingsRepository personSettingsRepository) {
        this.teamRepository = teamRepository;
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
        this.verifiers = verifiers;
        this.personSettingsRepository = personSettingsRepository;
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

    public void updatePerson(Person person) throws UserAlreadyExistException, InvalidNameException, InvalidPasswordException, InvalidUsernameException, InvalidEmailException {
        checkPersonData(person);

        personRepository.save(person);
    }

    public boolean passwordMatch(Person person, String plainPassword) {
        return passwordEncoder.matches(plainPassword, person.getPassword());
    }

    private Optional<PasswordVerifier> findPasswordVerifier() {
        for (PersonDataVerifier verifier : verifiers) {
            if (verifier.getClass().equals(PasswordVerifier.class)) return Optional.of((PasswordVerifier) verifier);
        }
        return Optional.empty();
    }

    public void updatePassword(Person person, String newPassword) throws InvalidPasswordException {
        PasswordVerifier verifier = findPasswordVerifier().orElseThrow(() -> new IllegalStateException("Password verifier is not defined"));

        person.setPassword(newPassword);

        verifier.verify(person);

        person.setPassword(passwordEncoder.encode(newPassword));
        personRepository.save(person);
    }

    public PersonSettings createOrGetSettingsOf(Person person) {
        Optional<PersonSettings> personSettings = personSettingsRepository.findByPerson(person);
        if (personSettings.isPresent()) return personSettings.get();

        PersonSettings toSave = new PersonSettings();
        toSave.setPerson(person);
        return personSettingsRepository.save(toSave);
    }

    public PersonSettings updatePersonSettings(PersonSettings personSettings) {
        return personSettingsRepository.save(personSettings);
    }
}
