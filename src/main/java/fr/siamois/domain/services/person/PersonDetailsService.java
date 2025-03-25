package fr.siamois.domain.services.person;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.infrastructure.database.repositories.auth.PersonRepository;
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
     * Load the user by its email
     *
     * @param email The email of the user
     * @return The user details
     * @throws UsernameNotFoundException Throws if the user is not found. Fails the authentication.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return findPersonByEmail(email);
    }

    /**
     * Find a person by its username
     *
     * @param email The email of the person
     * @return The Person
     * @throws UsernameNotFoundException Throws if the user is not found
     */
    public Person findPersonByEmail(String email) {
        return personRepository.findByMailIgnoreCase(email).orElseThrow(() -> new UsernameNotFoundException("Person with email " + email + " not found"));
    }


}
