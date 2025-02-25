package fr.siamois.infrastructure.repositories.specimen;

import fr.siamois.models.ArkEntity;
import fr.siamois.models.Institution;
import fr.siamois.models.specimen.SpecimenStudy;
import jakarta.validation.constraints.NotNull;
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

    List<? extends ArkEntity> findAllByArkIsNullAndCreatedByInstitution(@NotNull Institution createdByInstitution);
}

