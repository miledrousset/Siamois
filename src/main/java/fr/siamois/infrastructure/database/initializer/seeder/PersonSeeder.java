package fr.siamois.infrastructure.database.initializer.seeder;


import fr.siamois.domain.models.auth.Person;
import fr.siamois.infrastructure.database.repositories.person.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PersonSeeder {
    private final PersonRepository personRepository;

    public record PersonSpec(String email, String name, String lastname, String username) {
    }

    public Person findPersonOrReturnNull(String email) {
        return personRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(null);
    }

    private Person getOrCreatePerson(String email, String name, String lastname, String username) {
        Optional<Person> optAuthor = personRepository.findByEmailIgnoreCase(email);
        Person authorGetOrCreated ;
        if(optAuthor.isPresent()) {
            authorGetOrCreated = optAuthor.get();
        }
        else {
            authorGetOrCreated = new Person();
            authorGetOrCreated.setUsername(username);
            authorGetOrCreated.setName(name);
            authorGetOrCreated.setLastname(lastname);
            authorGetOrCreated.setEmail(email);
            authorGetOrCreated.setPassword("mysuperstrongpassword");
            personRepository.save(authorGetOrCreated);
        }
        return authorGetOrCreated;
    }

    public Map<String, Person> seed(List<PersonSpec> specs) {
        Map<String, Person> result = new HashMap<>();
        for (var s : specs) {
            result.put(s.email, getOrCreatePerson(s.email, s.name, s.lastname, s.username));
        }
        return result;
    }
}
