package fr.siamois.infrastructure.repositories.actionunit;

import fr.siamois.infrastructure.repositories.history.TraceableEntries;
import fr.siamois.models.actionunit.ActionUnit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface ActionUnitRepository extends CrudRepository<ActionUnit, Long>, TraceableEntries {

    List<ActionUnit> findAllBySpatialUnitId(Long id);

    @Query(
            nativeQuery = true,
            value = "SELECT au.* FROM action_unit au WHERE fk_author_id = :author AND creation_time BETWEEN :start AND :end"
    )
    List<ActionUnit> findAllCreatedBetweenByUser(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end, @Param("author") Long personId);

}
