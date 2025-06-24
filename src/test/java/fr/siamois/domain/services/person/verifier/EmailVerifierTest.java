package fr.siamois.domain.services.person.verifier;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.auth.InvalidEmailException;
import fr.siamois.infrastructure.database.repositories.person.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class EmailVerifierTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private EmailVerifier emailVerifier;

    @Test
    void verify_shouldThrowInvalidEmail_whenEmailIsInvalid() {
        Person person = new Person();
        person.setEmail("invalid-email");

        assertThrows(InvalidEmailException.class, () -> emailVerifier.verify(person));
    }

    @Test
    void verify_shouldNotThrowException_whenEmailIsValid() throws InvalidEmailException {
        Person person = new Person();
        person.setEmail("valid.email@example.com");

        emailVerifier.verify(person);
    }
}