package fr.siamois.domain.services.auth;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.infrastructure.repositories.auth.PersonRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * <p>This class is a service that handles the authentication of the application.</p>
 * <p>It implements the UserDetailsService interface from Spring Security to manage connection with database informations.</p>
 *
 * @author Julien Linget
 */
@Service
public class PersonDetailsService implements UserDetailsService {

    private final PersonRepository personRepository;

    public PersonDetailsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Load the user by its username.
     *
     * @param username The username of the user
     * @return The user details
     * @throws UsernameNotFoundException Throws if the user is not found. Fails the authentication.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findPersonByUsername(username);
    }

    /**
     * Find a person by its username
     *
     * @param username The username of the person
     * @return The Person
     * @throws UsernameNotFoundException Throws if the user is not found
     */
    public Person findPersonByUsername(String username) {
        return personRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new UsernameNotFoundException("Person with username " + username + " not found"));
    }


}
