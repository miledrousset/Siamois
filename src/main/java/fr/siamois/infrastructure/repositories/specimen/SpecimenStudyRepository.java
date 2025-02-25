package fr.siamois.infrastructure.repositories.specimen;

import fr.siamois.models.ArkEntity;
import fr.siamois.models.Institution;
import fr.siamois.models.specimen.SpecimenStudy;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecimenStudyRepository extends CrudRepository<SpecimenStudy, Long> {

    List<? extends ArkEntity> findAllByArkIsNullAndCreatedByInstitution(@NotNull Institution createdByInstitution);
}

