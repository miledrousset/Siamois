package fr.siamois.domain.services;

import fr.siamois.domain.models.Team;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.auth.*;
import fr.siamois.domain.services.auth.verifier.EmailVerifier;
import fr.siamois.domain.services.auth.verifier.PasswordVerifier;
import fr.siamois.domain.services.auth.verifier.UsernameVerifier;
import fr.siamois.infrastructure.repositories.auth.PersonRepository;
import fr.siamois.infrastructure.repositories.auth.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private TeamRepository teamRepository;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private final EmailVerifier emailVerifier = new EmailVerifier();
    private final PasswordVerifier passwordVerifier = new PasswordVerifier();

    private PersonService personService;

    @BeforeEach
    void setUp() {
        UsernameVerifier usernameVerifier = new UsernameVerifier(personRepository);
        personService = new PersonService(teamRepository, personRepository, passwordEncoder, List.of(usernameVerifier, emailVerifier, passwordVerifier));
    }

    @Test
    void findAllTeams() {
        List<Team> teams = new ArrayList<>();
        teams.add(new Team());
        when(teamRepository.findAll()).thenReturn(teams);

        List<Team> result = personService.findAllTeams();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void createPerson() throws InvalidUsernameException, UserAlreadyExistException, InvalidEmailException, InvalidPasswordException, InvalidNameException {
        String username = "test.user";
        String email = "test@example.com";
        String password = "password123";

        when(personRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(personRepository.save(any(Person.class))).then(invocation -> invocation.getArgument(0, Person.class));

        Person toSave = new Person();
        toSave.setUsername(username);
        toSave.setMail(email);
        toSave.setPassword(password);

        Person person = personService.createPerson(toSave);

        assertNotNull(person);
        assertEquals(username, person.getUsername());
        assertEquals(email, person.getMail());
        assertEquals("encodedPassword", person.getPassword());
    }

    @Test
    void findAllByNameLastnameContaining() {
        String nameOrLastname = "test";
        List<Person> persons = new ArrayList<>();
        persons.add(new Person());

        when(personRepository.findAllByNameOrLastname(nameOrLastname)).thenReturn(persons);

        List<Person> result = personService.findAllByNameLastnameContaining(nameOrLastname);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findById() {
        long id = 1L;
        Person person = new Person();
        when(personRepository.findById(id)).thenReturn(Optional.of(person));

        Person result = personService.findById(id);

        assertNotNull(result);
        assertEquals(person, result);
    }
}