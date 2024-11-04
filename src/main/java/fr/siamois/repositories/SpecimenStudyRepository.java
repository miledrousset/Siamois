package fr.siamois.repositories;

import fr.siamois.models.SpecimenGroup;
import fr.siamois.models.SpecimenStudy;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecimenStudyRepository extends CrudRepository<SpecimenStudy, Integer> {

    @Query(
            nativeQuery = true,
            value = "SELECT * FROM specimen_study ss WHERE ss.fk_specimen_group_id = :specimenGroup"
    )
    List<SpecimenStudy> findAllBySpecimenGroup(@Param("specimenGroup") SpecimenGroup specimenGroup);

}

