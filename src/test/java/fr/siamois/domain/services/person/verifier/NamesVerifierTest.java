package fr.siamois.domain.services.person.verifier;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.auth.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class NamesVerifierTest {

    private NamesVerifier namesVerifier;

    @BeforeEach
    void setUp() {
        namesVerifier = new NamesVerifier();
    }

    @Test
    void verify_shouldThrowInvalidNameException_whenNameIsTooLong() {
        Person person = new Person();
        person.setName("a".repeat(Person.NAME_MAX_LENGTH + 1));
        person.setLastname("ValidLastName");

        assertThrows(InvalidNameException.class, () -> namesVerifier.verify(person));
    }

    @Test
    void verify_shouldThrowInvalidNameException_whenLastNameIsTooLong() {
        Person person = new Person();
        person.setName("ValidName");
        person.setLastname("a".repeat(Person.NAME_MAX_LENGTH + 1));

        assertThrows(InvalidNameException.class, () -> namesVerifier.verify(person));
    }

    @Test
    void verify_shouldNotThrowException_whenNamesAreValid() throws UserAlreadyExistException, InvalidNameException, InvalidPasswordException, InvalidUsernameException, InvalidEmailException {
        Person person = new Person();
        person.setName("ValidName");
        person.setLastname("ValidLastName");

        namesVerifier.verify(person);
    }
}