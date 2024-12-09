package fr.siamois.services.auth;


import fr.siamois.infrastructure.repositories.PersonRepository;
import fr.siamois.models.auth.Person;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.Setter;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author Grégory Bliault
 */
@Setter
@Service
public class PersonService {

    private PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Find all the person where name or lastname match the string. Case is ignored.
     *
     * @param nameOrLastname The string to look for in name or username
     * @return The Person list
     */
    public List<Person> findAllByNameLastnameContaining(String nameOrLastname) {
        return personRepository.findAllByNameIsContainingIgnoreCaseOrLastnameIsContainingIgnoreCase(nameOrLastname, nameOrLastname);
    }

    public Person findById(long id) {
        return personRepository.findById(id).orElse(null);
    }
}
