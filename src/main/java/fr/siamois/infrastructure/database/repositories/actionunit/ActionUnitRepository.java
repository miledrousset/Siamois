package fr.siamois.infrastructure.database.repositories.actionunit;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.ark.Ark;
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
