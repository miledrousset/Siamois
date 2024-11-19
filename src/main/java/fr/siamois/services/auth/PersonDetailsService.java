package fr.siamois.services.auth;

import fr.siamois.infrastructure.repositories.PersonRepository;
import fr.siamois.models.Person;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Setter
@Service
public class PersonDetailsService implements UserDetailsService {

    private PersonRepository personRepository;

    public PersonDetailsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> personOptional = personRepository.findPersonByUsername(username);
        if (personOptional.isEmpty()) throw new UsernameNotFoundException("User with username " + username + " not found");
        return personOptional.get();
    }
}
