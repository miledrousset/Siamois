package fr.siamois.repositories;

import fr.siamois.models.Person;
import org.springframework.data.repository.CrudRepository;

public interface PersonRepository extends CrudRepository<Person, Integer> {


}
