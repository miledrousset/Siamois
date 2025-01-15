package fr.siamois.infrastructure.repositories;

import fr.siamois.infrastructure.repositories.history.TraceableEntries;
import fr.siamois.models.ActionUnit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface ActionUnitRepository extends CrudRepository<ActionUnit, Long>, TraceableEntries {

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

    List<ActionUnit> findAllBySpatialUnitId(Long id);

    @Query(
            nativeQuery = true,
            value = "SELECT au.* FROM action_unit au WHERE fk_author_id = :author AND creation_time BETWEEN :start AND :end"
    )
    List<ActionUnit> findAllCreatedBetweenByUser(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end, @Param("author") Long personId);
}
