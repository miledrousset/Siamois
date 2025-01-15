package fr.siamois.services.auth;

import fr.siamois.infrastructure.repositories.auth.PersonRepository;
import fr.siamois.models.auth.Person;
import fr.siamois.services.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonService personService;

    Person p ;

    @BeforeEach
    void setUp() {
        p = new Person();
    }

    @Test
    void findAllByNameLastnameContaining_Success() {

        when(personRepository.findAllByNameIsContainingIgnoreCaseOrLastnameIsContainingIgnoreCase("bob", "bob")).thenReturn(List.of(p));

        // Act
        List<Person> actualResult = personService.findAllByNameLastnameContaining("bob");

        // Assert
        assertEquals(List.of(p), actualResult);
    }

}