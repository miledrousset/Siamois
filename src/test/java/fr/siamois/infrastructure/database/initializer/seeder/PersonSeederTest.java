package fr.siamois.infrastructure.database.initializer.seeder;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.infrastructure.database.repositories.person.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonSeederTest {

    @Mock
    PersonRepository personRepository;

    @InjectMocks
    PersonSeeder seeder;

    @Test
    void seed_AlreadyExists() {
        Person p = new Person();
        List<PersonSeeder.PersonSpec> toInsert = List.of(
                new PersonSeeder.PersonSpec("user@siamois.fr", "name", "lastname", "username")
        );
        when(personRepository.findByEmailIgnoreCase("user@siamois.fr")).thenReturn(Optional.of(p));
        Map<String, Person> res = seeder.seed(toInsert);
        assertNotNull(res.get("user@siamois.fr"));
        verify(personRepository, never()).save(any(Person.class));
    }

    @Test
    void seed_DoesNotExists() {
        List<PersonSeeder.PersonSpec> toInsert = List.of(
                new PersonSeeder.PersonSpec("user@siamois.fr", "name", "lastname", "username")
        );
        when(personRepository.findByEmailIgnoreCase("user@siamois.fr")).thenReturn(Optional.empty());
        Map<String, Person> res = seeder.seed(toInsert);
        assertNotNull(res.get("user@siamois.fr"));
        verify(personRepository, times(1)).save(any(Person.class));
    }

}