package fr.siamois.repositories;

import fr.siamois.models.RecordingUnit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordingUnitRepository extends CrudRepository<RecordingUnit, Integer> {


}
