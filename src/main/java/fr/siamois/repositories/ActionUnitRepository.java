package fr.siamois.repositories;

import fr.siamois.models.ActionUnit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionUnitRepository extends CrudRepository<ActionUnit, Integer> {


}
