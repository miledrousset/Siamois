package fr.siamois.domain.services.auth.verifier;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.UserAlreadyExist;
import fr.siamois.domain.models.exceptions.auth.InvalidEmail;
import fr.siamois.domain.models.exceptions.auth.InvalidPassword;
import fr.siamois.domain.models.exceptions.auth.InvalidUsername;

public interface PersonDataVerifier {
    void verify(Person person) throws InvalidUsername, UserAlreadyExist, InvalidEmail, InvalidPassword;
}
