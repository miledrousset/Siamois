package fr.siamois.infrastructure.repositories;

import fr.siamois.models.SpecimenStudy;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecimenStudyRepository extends CrudRepository<SpecimenStudy, Long> {

    @Query(
            nativeQuery = true,
            value = "SELECT ss.* FROM specimen_study ss WHERE ss.fk_specimen_group_id = :specimenGroupId"
    )
    List<SpecimenStudy> findAllBySpecimenGroup(@Param("specimenGroupId") Long specimenGroupId);

}

