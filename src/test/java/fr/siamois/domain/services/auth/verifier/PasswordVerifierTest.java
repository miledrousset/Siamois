package fr.siamois.domain.services.auth.verifier;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.auth.InvalidPassword;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PasswordVerifierTest {

    private final PasswordVerifier passwordVerifier = new PasswordVerifier();

    @Test
    void verify_shouldThrowInvalidPassword_whenPasswordIsInvalid() {
        Person person = new Person();
        person.setPassword("short");

        assertThrows(InvalidPassword.class, () -> passwordVerifier.verify(person));
    }

    @Test
    void verify_shouldNotThrowException_whenPasswordIsValid() throws InvalidPassword {
        Person person = new Person();
        person.setPassword("validPassword123");

        passwordVerifier.verify(person);
    }
}