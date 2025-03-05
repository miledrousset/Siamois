package fr.siamois.domain.services.auth.verifier;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.auth.InvalidEmail;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailVerifierTest {

    private final EmailVerifier emailVerifier = new EmailVerifier();

    @Test
    void verify_shouldThrowInvalidEmail_whenEmailIsInvalid() {
        Person person = new Person();
        person.setMail("invalid-email");

        assertThrows(InvalidEmail.class, () -> emailVerifier.verify(person));
    }

    @Test
    void verify_shouldNotThrowException_whenEmailIsValid() throws InvalidEmail {
        Person person = new Person();
        person.setMail("valid.email@example.com");

        emailVerifier.verify(person);
    }
}