package fr.siamois.services.auth;

import fr.siamois.infrastructure.repositories.auth.PersonRepository;
import fr.siamois.models.auth.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonDetailsServiceTest {

    @Mock
    private PersonRepository personRepository;

    private PersonDetailsService personDetailsService;

    @BeforeEach
    void setUp() {
        personDetailsService = new PersonDetailsService(personRepository);
    }

    @Test
    void loadUserByUsername() {
        Person person = new Person();
        person.setUsername("testUser");

        when(personRepository.findPersonByUsername("testUser")).thenReturn(Optional.of(person));

        assertEquals(person, personDetailsService.loadUserByUsername("testUser"));
    }

    @Test
    void loadUserByUsername_throwsUsernameNotFoundException() {
        when(personRepository.findPersonByUsername("unknownUser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> personDetailsService.loadUserByUsername("unknownUser"));
    }

    @Test
    void findPersonByUsername() {
        Person person = new Person();
        person.setUsername("testUser");

        when(personRepository.findPersonByUsername("testUser")).thenReturn(Optional.of(person));

        assertEquals(person, personDetailsService.findPersonByUsername("testUser"));
    }

    @Test
    void findPersonByUsername_throwsUsernameNotFoundException() {
        when(personRepository.findPersonByUsername("unknownUser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> personDetailsService.findPersonByUsername("unknownUser"));
    }
}