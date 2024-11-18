package fr.siamois.utils;

import fr.siamois.models.Person;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuthenticatedUserUtils {

    /**
     * Retrieve the authenticated Person from the SecurityContextHolder
     * @return Optional contains Person if user is authenticated and empty if no user is authenticated
     */
    public Optional<Person> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( authentication == null || authentication instanceof AnonymousAuthenticationToken) return Optional.empty();
        Person person = (Person) authentication.getPrincipal();
        return Optional.of(person);
    }

}
