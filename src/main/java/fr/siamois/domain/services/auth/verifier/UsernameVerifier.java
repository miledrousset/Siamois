package fr.siamois.domain.services.auth.verifier;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.UserAlreadyExist;
import fr.siamois.domain.models.exceptions.auth.InvalidUsername;
import fr.siamois.infrastructure.repositories.auth.PersonRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Order(1)
public class UsernameVerifier implements PersonDataVerifier{
    private final PersonRepository personRepository;

    public UsernameVerifier(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public void verify(Person person) throws InvalidUsername, UserAlreadyExist {
        String username = person.getUsername();

        usernameHasValidLength(username);
        usernameHasValidChars(username);
        usernameDoesNotExist(username);
    }

    private void usernameDoesNotExist(String username) throws UserAlreadyExist {
        Optional<Person> optPerson = personRepository.findByUsernameIgnoreCase(username);
        if (optPerson.isPresent()) throw new UserAlreadyExist("Username already exists.");
    }

    private static void usernameHasValidLength(String username) throws InvalidUsername {
        if (StringUtils.isBlank(username)) throw new InvalidUsername("Username cannot be empty.");
        if (username.length() > Person.USERNAME_MAX_LENGTH) throw new InvalidUsername("Username should not exceed " + Person.USERNAME_MAX_LENGTH + " characters.");
    }

    private static void usernameHasValidChars(String username) throws InvalidUsername {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9.]+$");
        Matcher matcher = pattern.matcher(username);

        if (!matcher.find()) throw new InvalidUsername("Username must contain only letters, numbers and dots.");
    }
}
