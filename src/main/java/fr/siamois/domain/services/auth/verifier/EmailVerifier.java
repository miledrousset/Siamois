package fr.siamois.domain.services.auth.verifier;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.auth.InvalidEmail;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Order(2)
public class EmailVerifier implements PersonDataVerifier {

    @Override
    public void verify(Person person) throws InvalidEmail {
        String email = person.getMail();

        emailUsesValidChars(email);
        checkMailLength(email);
    }

    private static void emailUsesValidChars(String email) throws InvalidEmail {
        Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        Matcher emailMatcher = emailPattern.matcher(email);
        if (!emailMatcher.find()) throw new InvalidEmail("Email is not valid.");
    }

    private static void checkMailLength(String email) throws InvalidEmail {
        if (StringUtils.isBlank(email)) throw new InvalidEmail("Email cannot be empty.");
        String[] splitMail = email.split("@");
        if (splitMail[0].length() > Person.LOCAL_MAIL_MAX_LENGTH) throw new InvalidEmail("Local part of the email should not exceed "
                + Person.LOCAL_MAIL_MAX_LENGTH + " characters.");
        if (splitMail[1].length() > Person.DOMAIN_MAIL_MAX_LENGTH) throw new InvalidEmail("Domain part of the email should not exceed "
                + Person.DOMAIN_MAIL_MAX_LENGTH + " characters.");
    }

}
