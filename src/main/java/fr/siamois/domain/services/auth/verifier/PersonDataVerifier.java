package fr.siamois.domain.services.auth.verifier;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.auth.*;

public interface PersonDataVerifier {
    void verify(Person person) throws InvalidUsernameException, UserAlreadyExistException, InvalidEmailException, InvalidPasswordException, InvalidNameException;
}
