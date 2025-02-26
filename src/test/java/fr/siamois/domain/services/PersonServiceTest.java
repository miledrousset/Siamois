package fr.siamois.domain.services;

import fr.siamois.domain.models.Team;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.auth.SystemRole;
import fr.siamois.domain.models.exceptions.UserAlreadyExist;
import fr.siamois.domain.models.exceptions.auth.InvalidEmail;
import fr.siamois.domain.models.exceptions.auth.InvalidPassword;
import fr.siamois.domain.models.exceptions.auth.InvalidUsername;
import fr.siamois.infrastructure.repositories.auth.PersonRepository;
import fr.siamois.infrastructure.repositories.auth.SystemRoleRepository;
import fr.siamois.infrastructure.repositories.auth.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private SystemRoleRepository systemRoleRepository;

    @InjectMocks
    private PersonService personService;

    @BeforeEach
    void setUp() {
        personService = new PersonService(teamRepository, personRepository, passwordEncoder, systemRoleRepository);
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
    void createPerson() throws InvalidUsername, UserAlreadyExist, InvalidEmail, InvalidPassword {
        String username = "test.user";
        String email = "test@example.com";
        String password = "password123";

        when(personRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(personRepository.save(any(Person.class))).then(invocation -> invocation.getArgument(0, Person.class));

        Person person = personService.createPerson(username, email, password);

        assertNotNull(person);
        assertEquals(username, person.getUsername());
        assertEquals(email, person.getMail());
        assertEquals("encodedPassword", person.getPassword());
    }

    @Test
    void addPersonToTeamManagers() {
        Person person = new Person();
        SystemRole role = new SystemRole();
        role.setRoleName("TEAM_MANAGER");

        when(systemRoleRepository.findSystemRoleByRoleNameIgnoreCase("TEAM_MANAGER")).thenReturn(Optional.of(role));

        personService.addPersonToTeamManagers(person);

        assertTrue(person.getRoles().contains(role));
        verify(personRepository).save(person);
    }

    @Test
    void findAllByNameLastnameContaining() {
        String nameOrLastname = "test";
        List<Person> persons = new ArrayList<>();
        persons.add(new Person());

        when(personRepository.findAllByNameIsContainingIgnoreCaseOrLastnameIsContainingIgnoreCase(nameOrLastname, nameOrLastname)).thenReturn(persons);

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