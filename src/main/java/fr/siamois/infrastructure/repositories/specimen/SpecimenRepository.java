package fr.siamois.infrastructure.repositories.specimen;

import fr.siamois.infrastructure.repositories.history.TraceableEntries;
import fr.siamois.models.specimen.Specimen;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface SpecimenRepository extends CrudRepository<Specimen, Long>, TraceableEntries {

    @Query(
            nativeQuery = true,
            value = "SELECT s.* FROM specimen s JOIN specimen_group_attribution sga ON s.specimen_id = sga.fk_specimen_id WHERE sga.fk_specimen_group_id = :specimenGroupId"
    )
    List<Specimen> findAllSpecimensOfSpecimenGroup(@Param("specimenGroupId") Long specimenGroupId);

    @Query(
            nativeQuery = true,
            value = "SELECT s.* FROM specimen s WHERE fk_author_id = :author AND creation_time BETWEEN :start AND :end"
    )
    List<Specimen> findAllCreatedBetweenByUser(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end, @Param("author") Long personId);

}

