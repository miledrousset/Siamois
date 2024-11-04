package fr.siamois.repositories;

import fr.siamois.models.Specimen;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecimenRepository extends CrudRepository<Specimen, Integer> {


}

