package fr.siamois.infrastructure.repositories.specimen;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.specimen.SpecimenStudy;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecimenStudyRepository extends CrudRepository<SpecimenStudy, Long> {

    List<SpecimenStudy> findAllByArkIsNullAndCreatedByInstitution(@NotNull Institution createdByInstitution);
}

