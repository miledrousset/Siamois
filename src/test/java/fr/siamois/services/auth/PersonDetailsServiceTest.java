package fr.siamois.services.auth;

import fr.siamois.models.Person;
import fr.siamois.repositories.PersonRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
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
    void loadUserByUsername_shouldReturnPerson_whenUsernameExist() {
        Person person = new Person();
        person.setId(-1);
        person.setUsername("test_username");
        person.setPassword("unhashed_password");

        when(personRepository.findPersonByUsername("test_username")).thenReturn(Optional.of(person));

        UserDetails result = personDetailsService.loadUserByUsername("test_username");

        assertEquals("test_username", result.getUsername());
        assertEquals("unhashed_password", result.getPassword());
    }

    @Test
    void loadUserByUsername_shouldThrowError_whenUsernameDoesNotExist() {
        assertThrows(UsernameNotFoundException.class, () -> {
            when(personRepository.findPersonByUsername(anyString())).thenReturn(Optional.empty());
            personDetailsService.loadUserByUsername("test_username");
        });
    }
}