package fr.siamois.repositories;

import fr.siamois.models.Concept;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConceptRepository extends CrudRepository<Concept, Integer> {


}
