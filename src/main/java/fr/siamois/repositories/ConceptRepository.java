package fr.siamois.repositories;

import fr.siamois.models.Concept;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConceptRepository extends CrudRepository<Concept, Long> {

    @Query(
            nativeQuery = true,
            value = "SELECT c.* FROM concept c JOIN specimen_study_typology sst on c.concept_id = sst.fk_typology_concept_id WHERE sst.fk_specimen_study_id = :actionUnitId"
    )
    List<Concept> findAllTypologiesOfSpecimenStudy(@Param("actionUnitId") Long specimenStudyId);

    @Query(
            nativeQuery = true,
            value = "SELECT c.* FROM concept c JOIN ru_study_typology rust on c.concept_id = rust.fk_typology_concept_id WHERE rust.fk_ru_study_id = :recordingUnitStudyId"
    )
    List<Concept> findAllTypologiesOfRecordingUnitStudy(@Param("recordingUnitStudyId") Long recordingUnitStudyId);

}
