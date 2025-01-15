package fr.siamois.infrastructure.repositories.specimen;

import fr.siamois.models.specimen.SpecimenStudy;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface SpecimenStudyRepository extends CrudRepository<SpecimenStudy, Long> {

    @Query(
            nativeQuery = true,
            value = "SELECT ss.* FROM specimen_study ss WHERE ss.fk_specimen_group_id = :specimenGroupId"
    )
    List<SpecimenStudy> findAllBySpecimenGroup(@Param("specimenGroupId") Long specimenGroupId);

    @Query(
            nativeQuery = true,
            value = "SELECT s.* FROM specimen_study s WHERE fk_author_id = :author AND creation_time BETWEEN :start AND :end"
    )
    List<SpecimenStudy> findAllCreatedBetweenByUser(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end, @Param("author") Long personId);

}

