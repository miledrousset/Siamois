package fr.siamois.repositories;

import fr.siamois.models.SpecimenStudy;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecimenStudyRepository extends CrudRepository<SpecimenStudy, Integer> {


}

