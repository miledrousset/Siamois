package fr.siamois.repositories;

import fr.siamois.models.SpatialUnit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpatialUnitRepository extends CrudRepository<SpatialUnit, Integer> {


}

