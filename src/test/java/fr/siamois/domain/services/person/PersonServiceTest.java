package fr.siamois.domain.services.person;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.auth.PendingPerson;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.auth.*;
import fr.siamois.domain.models.settings.PersonSettings;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.services.LangService;
import fr.siamois.domain.services.person.verifier.EmailVerifier;
import fr.siamois.domain.services.person.verifier.PasswordVerifier;
import fr.siamois.domain.services.person.verifier.PersonDataVerifier;
import fr.siamois.infrastructure.database.repositories.person.PendingPersonRepository;
import fr.siamois.infrastructure.database.repositories.person.PersonRepository;
import fr.siamois.infrastructure.database.repositories.settings.PersonSettingsRepository;
import fr.siamois.ui.email.EmailManager;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock private PersonRepository personRepository;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    private final EmailVerifier emailVerifier = new EmailVerifier();
    private final PasswordVerifier passwordVerifier = new PasswordVerifier();
    @Mock private PersonSettingsRepository personSettingsRepository;
    @Mock private InstitutionService institutionService;
    @Mock private LangService langService;
    @Mock private EmailManager emailManager;
    @Mock private PendingPersonRepository pendingPersonRepository;
    @Mock private HttpServletRequest httpServletRequest;

    private PersonService personService;

    Person person;

    @BeforeEach
    void setUp() {
        person = new Person();
        person.setId(1L);
        person.setPassword("password");
        person.setMail("mail@localhost.com");

        List<PersonDataVerifier> verifiers = List.of(passwordVerifier, emailVerifier);

        personService = new PersonService(
                personRepository,
                passwordEncoder,
                verifiers,
                personSettingsRepository,
                institutionService,
                langService,
                emailManager,
                pendingPersonRepository,
                httpServletRequest
        );
    }

    @Test
    void findAllByNameLastnameContaining_Success() {
        when(personRepository.findAllByNameOrLastname("bob")).thenReturn(List.of(person));

        // Act
        List<Person> actualResult = personService.findAllByNameLastnameContaining("bob");

        // Assert
        assertEquals(List.of(person), actualResult);
    }

    @Test
    void updatePerson_Success() throws UserAlreadyExistException, InvalidNameException, InvalidPasswordException, InvalidUsernameException, InvalidEmailException {
        // Arrange
        when(personRepository.save(person)).thenReturn(person);

        // Act
        personService.updatePerson(person);

        // Assert
        verify(personRepository, times(1)).save(person);
    }

    @Test
    void passwordMatch_Success() {
        // Arrange
        when(passwordEncoder.matches("plainPassword", "encodedPassword")).thenReturn(true);
        person.setPassword("encodedPassword");

        // Act
        boolean result = personService.passwordMatch(person, "plainPassword");

        // Assert
        assertTrue(result);
    }

    @Test
    void updatePassword_Success() throws InvalidPasswordException {
        // Arrange

        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        // Act
        personService.updatePassword(person, "newPassword");

        // Assert
        assertEquals("encodedNewPassword", person.getPassword());
    }

    @Test
    void createOrGetSettingsOf() {
        person = new Person();
        person.setId(1L);
        PersonSettings settings = new PersonSettings();
        settings.setPerson(person);

        when(personSettingsRepository.findByPerson(person)).thenReturn(Optional.empty());
        when(personSettingsRepository.save(any(PersonSettings.class))).thenReturn(settings);

        PersonSettings result = personService.createOrGetSettingsOf(person);

        assertNotNull(result);
        assertEquals(person, result.getPerson());
        verify(personSettingsRepository).save(any(PersonSettings.class));
    }

    @Test
    void updatePersonSettings() {
        PersonSettings settings = new PersonSettings();
        when(personSettingsRepository.save(settings)).thenReturn(settings);

        PersonSettings result = personService.updatePersonSettings(settings);

        assertNotNull(result);
        assertEquals(settings, result);
        verify(personSettingsRepository).save(settings);
    }

    @Test
    void createPerson_Success() throws Exception {
        when(personRepository.save(any(Person.class))).thenReturn(person);
        when(pendingPersonRepository.findByEmail(person.getMail())).thenReturn(Optional.empty());

        Person result = personService.createPerson(person);

        assertNotNull(result);
        verify(personRepository, times(1)).save(person);
        verify(pendingPersonRepository, times(1)).findByEmail(person.getMail());
    }

    @Test
    void createPerson_ThrowsInvalidEmailException() {
        person.setMail("invalid-email");
        assertThrows(InvalidEmailException.class, () -> personService.createPerson(person));
    }

    @Test
    void findById_Success() {
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));

        Person result = personService.findById(1L);

        assertNotNull(result);
        assertEquals(person, result);
        verify(personRepository, times(1)).findById(1L);
    }

    @Test
    void findById_NotFound() {
        when(personRepository.findById(1L)).thenReturn(Optional.empty());

        Person result = personService.findById(1L);

        assertNull(result);
        verify(personRepository, times(1)).findById(1L);
    }

    @Test
    void findPasswordVerifier_Success() {
        Optional<PasswordVerifier> verifier = personService.findPasswordVerifier();

        assertTrue(verifier.isPresent());
        assertEquals(passwordVerifier, verifier.get());
    }

    @Test
    void generateToken_Success() {
        when(pendingPersonRepository.existsByRegisterToken(anyString())).thenReturn(false);

        String token = personService.generateToken();

        assertNotNull(token);
        assertEquals(20, token.length());
        verify(pendingPersonRepository, atLeastOnce()).existsByRegisterToken(anyString());
    }

    @Test
    void invitationLink_Success() {
        PendingPerson pendingPerson = new PendingPerson();
        pendingPerson.setRegisterToken("testToken");

        when(httpServletRequest.getScheme()).thenReturn("https");
        when(httpServletRequest.getServerName()).thenReturn("example.com");
        when(httpServletRequest.getServerPort()).thenReturn(443);
        when(httpServletRequest.getContextPath()).thenReturn("/app");

        String link = personService.invitationLink(pendingPerson);

        assertEquals("https://example.com/app/register/testToken", link);
    }

    @Test
    void findPendingByToken_Success() {
        PendingPerson pendingPerson = new PendingPerson();
        when(pendingPersonRepository.findByRegisterToken("testToken")).thenReturn(Optional.of(pendingPerson));

        Optional<PendingPerson> result = personService.findPendingByToken("testToken");

        assertTrue(result.isPresent());
        assertEquals(pendingPerson, result.get());
        verify(pendingPersonRepository, times(1)).findByRegisterToken("testToken");
    }

    @Test
    void findByEmail_Success() {
        when(personRepository.findByMailIgnoreCase("test@example.com")).thenReturn(Optional.of(person));

        Optional<Person> result = personService.findByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals(person, result.get());
        verify(personRepository, times(1)).findByMailIgnoreCase("test@example.com");
    }

    @Test
    void deletePending_Success() {
        PendingPerson pendingPerson = new PendingPerson();

        personService.deletePending(pendingPerson);

        verify(pendingPersonRepository, times(1)).delete(pendingPerson);
    }

    @Test
    void createPendingManager_ShouldReturnTrue_WhenSuccess() {
        // Arrange
        PendingPerson pendingPerson = new PendingPerson();
        pendingPerson.setEmail("test@example.com");
        pendingPerson.setInstitution(new Institution());
        when(personRepository.existsByMail(pendingPerson.getEmail())).thenReturn(false);
        when(pendingPersonRepository.save(any(PendingPerson.class))).thenReturn(pendingPerson);

        // Act
        boolean result = personService.createPendingManager(pendingPerson);

        // Assert
        assertTrue(result);
        verify(pendingPersonRepository, times(1)).save(pendingPerson);
        verify(emailManager, times(1)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void createPendingManager_ShouldReturnFalse_WhenEmailExists() {
        // Arrange
        PendingPerson pendingPerson = new PendingPerson();
        pendingPerson.setEmail("test@example.com");
        when(personRepository.existsByMail(pendingPerson.getEmail())).thenReturn(true);

        // Act
        boolean result = personService.createPendingManager(pendingPerson);

        // Assert
        assertFalse(result);
        verify(pendingPersonRepository, never()).save(any(PendingPerson.class));
        verify(emailManager, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void createPendingManager_ShouldReturnFalse_WhenExceptionThrown() {
        // Arrange
        PendingPerson pendingPerson = new PendingPerson();
        pendingPerson.setEmail("test@example.com");
        when(personRepository.existsByMail(pendingPerson.getEmail())).thenReturn(false);
        when(pendingPersonRepository.save(any(PendingPerson.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        boolean result = personService.createPendingManager(pendingPerson);

        // Assert
        assertFalse(result);
        verify(pendingPersonRepository, times(1)).save(pendingPerson);
        verify(emailManager, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void findPasswordVerifier_ShouldReturnVerifier_WhenPresent() {
        // Act
        Optional<PasswordVerifier> verifier = personService.findPasswordVerifier();

        // Assert
        assertTrue(verifier.isPresent());
        assertEquals(PasswordVerifier.class, verifier.get().getClass());
    }

    @Test
    void findPasswordVerifier_ShouldReturnEmpty_WhenNotPresent() {
        // Arrange
        List<PersonDataVerifier> verifiers = List.of(); // Liste vide
        personService = new PersonService(
                personRepository,
                passwordEncoder,
                verifiers,
                personSettingsRepository,
                institutionService,
                langService,
                emailManager,
                pendingPersonRepository,
                httpServletRequest
        );

        // Act
        Optional<PasswordVerifier> verifier = personService.findPasswordVerifier();

        // Assert
        assertTrue(verifier.isEmpty());
    }

}