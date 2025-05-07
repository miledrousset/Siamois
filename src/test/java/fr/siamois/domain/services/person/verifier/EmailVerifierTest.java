package fr.siamois.domain.services.person.verifier;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.auth.InvalidEmailException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailVerifierTest {

    private final EmailVerifier emailVerifier = new EmailVerifier();

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