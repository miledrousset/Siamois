package fr.siamois.repositories;

import fr.siamois.models.ActionUnit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionUnitRepository extends CrudRepository<ActionUnit, Long> {

    @Query(
            nativeQuery = true,
            value = "SELECT au.* FROM action_unit au JOIN action_hierarchy auh ON au.action_unit_id = auh.fk_child_id WHERE auh.fk_parent_id = :actionUnitId"
    )
    List<ActionUnit> findAllChildOfActionUnit(@Param("actionUnitId") Long actionUnitId);

    @Query(
            nativeQuery = true,
            value = "SELECT au.* FROM action_unit au JOIN action_hierarchy auh ON au.action_unit_id = auh.fk_parent_id WHERE auh.fk_child_id = :actionUnitId"
    )
    List<ActionUnit> findAllParentsOfActionUnit(@Param("actionUnitId") Long actionUnitId);

    List<ActionUnit> findAllBySpatialUnitId(Integer id);
}
