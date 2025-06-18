package fr.siamois.domain.services.person;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.auth.pending.PendingActionUnitAttribution;
import fr.siamois.domain.models.auth.pending.PendingInstitutionInvite;
import fr.siamois.domain.models.auth.pending.PendingPerson;
import fr.siamois.domain.models.exceptions.auth.*;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.settings.PersonSettings;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.services.LangService;
import fr.siamois.domain.services.auth.PendingPersonService;
import fr.siamois.domain.services.person.verifier.PasswordVerifier;
import fr.siamois.domain.services.person.verifier.PersonDataVerifier;
import fr.siamois.infrastructure.database.repositories.person.PendingPersonRepository;
import fr.siamois.infrastructure.database.repositories.person.PersonRepository;
import fr.siamois.infrastructure.database.repositories.settings.PersonSettingsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service to manage Person
 */
@Slf4j
@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final List<PersonDataVerifier> verifiers;
    private final PersonSettingsRepository personSettingsRepository;
    private final InstitutionService institutionService;
    private final LangService langService;
    private final PendingPersonRepository pendingPersonRepository;
    private final PendingPersonService pendingPersonService;
    private final fr.siamois.infrastructure.database.repositories.person.PendingInstitutionInviteRepository pendingInstitutionInviteRepository;

    public PersonService(PersonRepository personRepository,
                         BCryptPasswordEncoder passwordEncoder,
                         List<PersonDataVerifier> verifiers,
                         PersonSettingsRepository personSettingsRepository,
                         InstitutionService institutionService,
                         LangService langService,
                         PendingPersonRepository pendingPersonRepository,
                         PendingPersonService pendingPersonService, fr.siamois.infrastructure.database.repositories.person.PendingInstitutionInviteRepository pendingInstitutionInviteRepository) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
        this.verifiers = verifiers;
        this.personSettingsRepository = personSettingsRepository;
        this.institutionService = institutionService;
        this.langService = langService;
        this.pendingPersonRepository = pendingPersonRepository;
        this.pendingPersonService = pendingPersonService;
        this.pendingInstitutionInviteRepository = pendingInstitutionInviteRepository;
    }

    private void createAndDeletePendingRelations(PendingPerson pendingPerson, Person person) {
        Set<PendingInstitutionInvite> institutionInvites = pendingInstitutionInviteRepository.findAllByPendingPerson(pendingPerson);
        for (PendingInstitutionInvite invite : institutionInvites) {
            Institution institution = invite.getInstitution();
            if (invite.isManager()) {
                institutionService.addToManagers(institution, person);
            }
            if (invite.isActionManager()) {
                institutionService.addPersonToActionManager(institution, person);
            }

            Set<PendingActionUnitAttribution> attributions = pendingPersonService.findActionAttributionsByPendingInvite(invite);
            for (PendingActionUnitAttribution attribution : attributions) {
                institutionService.addPersonToActionUnit(attribution.getActionUnit(), person, attribution.getRole());
                pendingPersonService.delete(attribution);
            }

            pendingPersonService.delete(invite);
        }
    }

    private void managePendingInvites(Person savedPerson) {
        PendingPerson p = pendingPersonService.createOrGetPendingPerson(savedPerson.getEmail());
        createAndDeletePendingRelations(p, savedPerson);
        pendingPersonRepository.delete(p);
    }

    public Person createPerson(Person person) throws InvalidUsernameException, InvalidEmailException, UserAlreadyExistException, InvalidPasswordException, InvalidNameException {
        person.setId(-1L);

        checkPersonData(person);

        person.setPassword(passwordEncoder.encode(person.getPassword()));

        person = personRepository.save(person);

        managePendingInvites(person);

        return person;
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
     * Find all the person being an author of a spatial unit
     *
     * @param institution The institution
     * @return The Person list
     */
    public List<Person> findAllAuthorsOfSpatialUnitByInstitution(Institution institution) {
        return personRepository.findAllAuthorsOfSpatialUnitByInstitution(institution.getId());
    }

    /**
     * Find all the person being an author of a action unit
     *
     * @param institution The institution
     * @return The Person list
     */
    public List<Person> findAllAuthorsOfActionUnitByInstitution(Institution institution) {
        return personRepository.findAllAuthorsOfActionUnitByInstitution(institution.getId());
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

    Optional<PasswordVerifier> findPasswordVerifier() {
        for (PersonDataVerifier verifier : verifiers) {
            if (verifier.getClass().equals(PasswordVerifier.class)) return Optional.of((PasswordVerifier) verifier);
        }
        return Optional.empty();
    }

    public void updatePassword(Person person, String newPassword) throws InvalidPasswordException {
        PasswordVerifier verifier = findPasswordVerifier().orElseThrow(() -> new IllegalStateException("Password verifier is not defined"));

        person.setPassword(newPassword);
        person.setPassToModify(false);

        verifier.verify(person);

        person.setPassword(passwordEncoder.encode(newPassword));
        personRepository.save(person);
    }

    public PersonSettings createOrGetSettingsOf(Person person) {
        Optional<PersonSettings> personSettings = personSettingsRepository.findByPerson(person);
        if (personSettings.isPresent()) return personSettings.get();

        PersonSettings toSave = new PersonSettings();
        toSave.setPerson(person);
        toSave.setDefaultInstitution(findDefaultInstitution(person));
        toSave.setLangCode(findDefaultLang());

        return personSettingsRepository.save(toSave);
    }

    private String findDefaultLang() {
        return langService.getDefaultLang();
    }

    private Institution findDefaultInstitution(Person person) {
        Set<Institution> institutions = institutionService.findInstitutionsOfPerson(person);
        return institutions.isEmpty() ? null : institutions.stream().findFirst().orElseThrow(IllegalStateException::new);
    }

    public PersonSettings updatePersonSettings(PersonSettings personSettings) {
        log.trace("Updating person settings {}", personSettings.getPerson().getUsername());
        return personSettingsRepository.save(personSettings);
    }

    public Optional<Person> findByEmail(String email) {
        return personRepository.findByEmailIgnoreCase(email);
    }

    public List<Person> findClosestByUsernameOrEmail(String usernameOrMailInput) {
        if (usernameOrMailInput == null || usernameOrMailInput.isBlank()) {
            return List.of();
        }

        Set<Person> result = new HashSet<>();
        result.addAll(personRepository.findClosestByEmailLimit10(usernameOrMailInput));
        result.addAll(personRepository.findClosestByUsernameLimit10(usernameOrMailInput));

        return result.stream().toList();
    }
}
