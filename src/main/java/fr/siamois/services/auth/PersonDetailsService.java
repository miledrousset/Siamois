package fr.siamois.services.auth;

import fr.siamois.infrastructure.repositories.PersonRepository;
import fr.siamois.models.auth.Person;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * <p>This class is a service that handles the authentication of the application.</p>
 * <p>It implements the UserDetailsService interface from Spring Security to manage connection with database informations.</p>
 * @author Julien Linget
 */
@Setter
@Service
public class PersonDetailsService implements UserDetailsService {

    private PersonRepository personRepository;

    public PersonDetailsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Load the user by its username.
     * @param username The username of the user
     * @return The user details
     * @throws UsernameNotFoundException Throws if the user is not found. Fails the authentication.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> personOptional = personRepository.findPersonByUsername(username);
        if (personOptional.isEmpty()) throw new UsernameNotFoundException("User with username " + username + " not found");
        return personOptional.get();
    }
}
