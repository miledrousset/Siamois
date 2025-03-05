package fr.siamois.domain.services.auth.verifier;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.auth.UserAlreadyExistException;
import fr.siamois.domain.models.exceptions.auth.InvalidEmailException;
import fr.siamois.domain.models.exceptions.auth.InvalidNameException;
import fr.siamois.domain.models.exceptions.auth.InvalidPasswordException;
import fr.siamois.domain.models.exceptions.auth.InvalidUsernameException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(4)
public class NamesVerifier implements PersonDataVerifier {

    private void validateNameLength(String name, String fieldName) throws InvalidNameException {
        if (name != null && name.length() > Person.NAME_MAX_LENGTH) {
            throw new InvalidNameException(fieldName + " should be at most " + Person.NAME_MAX_LENGTH + " characters.");
        }
    }

    @Override
    public void verify(Person person) throws InvalidUsernameException, UserAlreadyExistException, InvalidEmailException, InvalidPasswordException, InvalidNameException {
        validateNameLength(person.getName(), "Name");
        validateNameLength(person.getLastname(), "Last name");
    }
}
