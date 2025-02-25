package fr.siamois.infrastructure.repositories.actionunit;

import fr.siamois.models.Institution;
import fr.siamois.models.actionunit.ActionUnit;
import fr.siamois.models.ark.Ark;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActionUnitRepository extends CrudRepository<ActionUnit, Long> {

    List<ActionUnit> findAllBySpatialUnitId(Long id);

    Optional<ActionUnit> findByArk(Ark ark);

    List<ActionUnit> findAllByArkIsNullAndCreatedByInstitution(@NotNull Institution createdByInstitution);
}
