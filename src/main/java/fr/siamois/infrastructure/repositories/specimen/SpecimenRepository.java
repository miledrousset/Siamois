package fr.siamois.infrastructure.repositories.specimen;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.specimen.Specimen;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecimenRepository extends CrudRepository<Specimen, Long> {
    List<Specimen> findAllByArkIsNullAndCreatedByInstitution(@NotNull Institution createdByInstitution);
}

