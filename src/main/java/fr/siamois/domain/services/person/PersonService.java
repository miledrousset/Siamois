package fr.siamois.domain.services.person;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.auth.PendingPerson;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.auth.*;
import fr.siamois.domain.models.settings.PersonSettings;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.services.LangService;
import fr.siamois.domain.services.person.verifier.PasswordVerifier;
import fr.siamois.domain.services.person.verifier.PersonDataVerifier;
import fr.siamois.domain.utils.DateUtils;
import fr.siamois.infrastructure.database.repositories.person.PendingPersonRepository;
import fr.siamois.infrastructure.database.repositories.person.PersonRepository;
import fr.siamois.infrastructure.database.repositories.settings.PersonSettingsRepository;
import fr.siamois.ui.email.EmailManager;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
    private final EmailManager emailManager;
    private final PendingPersonRepository pendingPersonRepository;

    public static final int MAX_GENERATION = 1000;
    private final HttpServletRequest httpServletRequest;

    private final Random random = new SecureRandom();

    public PersonService(PersonRepository personRepository,
                         BCryptPasswordEncoder passwordEncoder,
                         List<PersonDataVerifier> verifiers,
                         PersonSettingsRepository personSettingsRepository, InstitutionService institutionService, LangService langService, EmailManager emailManager, PendingPersonRepository pendingPersonRepository, HttpServletRequest httpServletRequest) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
        this.verifiers = verifiers;
        this.personSettingsRepository = personSettingsRepository;
        this.institutionService = institutionService;
        this.langService = langService;
        this.emailManager = emailManager;
        this.pendingPersonRepository = pendingPersonRepository;
        this.httpServletRequest = httpServletRequest;
    }

    public Person createPerson(Person person) throws InvalidUsernameException, InvalidEmailException, UserAlreadyExistException, InvalidPasswordException, InvalidNameException {
        person.setId(-1L);

        checkPersonData(person);

        person.setPassword(passwordEncoder.encode(person.getPassword()));

        person = personRepository.save(person);

        Optional<PendingPerson> pendingPerson = pendingPersonRepository.findByEmail((person.getMail()));
        pendingPerson.ifPresent(pendingPersonRepository::delete);

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
        List<Institution> institutions = institutionService.findInstitutionsOfPerson(person);
        return institutions.isEmpty() ? null : institutions.get(0);
    }

    public PersonSettings updatePersonSettings(PersonSettings personSettings) {
        log.trace("Updating person settings {}", personSettings);
        return personSettingsRepository.save(personSettings);
    }

    private String generateToken() {
        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder token;
        int attempts = 0;

        do {
            attempts++;
            token = new StringBuilder();
            for (int i = 0; i < 20; i++) {
                int randomIndex = random.nextInt(allowedChars.length());
                token.append(allowedChars.charAt(randomIndex));
            }
        } while (attempts < MAX_GENERATION && pendingPersonRepository.existsByRegisterToken(token.toString()));

        if (attempts == MAX_GENERATION) {
            throw new IllegalStateException("Unable to generate a unique token after " + MAX_GENERATION + " attempts");
        }

        return token.toString();
    }

    private String invitationLink(PendingPerson pendingPerson) {
        String domain = httpServletRequest.getScheme() + "://" + httpServletRequest.getServerName() +
                (httpServletRequest.getServerPort() != 80 && httpServletRequest.getServerPort() != 443 ? ":" + httpServletRequest.getServerPort() : "");
        return String.format("%s%s/register/%s", domain, httpServletRequest.getContextPath(), pendingPerson.getRegisterToken());
    }

    public boolean createPendingManager(PendingPerson pendingPerson) {
        if (personRepository.existsByMail(pendingPerson.getEmail())) {
            return false;
        }

        try {
            pendingPerson.setId(-1L);
            pendingPerson.setPendingInvitationExpirationDate(OffsetDateTime.now().plusHours(6));
            pendingPerson.setRegisterToken(generateToken());
            PendingPerson saved = pendingPersonRepository.save(pendingPerson);
            String emailBody = """
               Bonjour,
                Vous avez été invité à rejoindre l'application Siamois en tant que responsable de l'institution %s.
                Cliquez sur le lien suivant pour vous inscrire : %s
                Expiration de l'invitation le %s
               \s""";
            emailBody = String.format(emailBody, pendingPerson.getInstitution().getName(), invitationLink(saved), DateUtils.formatOffsetDateTime(saved.getPendingInvitationExpirationDate()));
            String subject = String.format("[SIAMOIS] Invitation à rejoindre %s", pendingPerson.getInstitution().getName());

            emailManager.sendEmail(pendingPerson.getEmail(), subject, emailBody);

            return true;
        } catch (Exception e) {
            log.error("Error while creating pending manager", e);
            return false;
        }
    }

    public Optional<PendingPerson> findPendingByToken(String token) {
        return pendingPersonRepository.findByRegisterToken(token);
    }
}
