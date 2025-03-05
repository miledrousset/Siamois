package fr.siamois.domain.services.auth.verifier;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.auth.UserAlreadyExistException;
import fr.siamois.domain.models.exceptions.auth.InvalidEmailException;
import fr.siamois.domain.models.exceptions.auth.InvalidNameException;
import fr.siamois.domain.models.exceptions.auth.InvalidPasswordException;
import fr.siamois.domain.models.exceptions.auth.InvalidUsernameException;

public interface PersonDataVerifier {
    void verify(Person person) throws InvalidUsernameException, UserAlreadyExistException, InvalidEmailException, InvalidPasswordException, InvalidNameException;
}
