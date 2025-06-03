package fr.siamois.infrastructure.database.repositories.person;

import fr.siamois.domain.models.auth.pending.PendingActionUnitAttribution;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingActionUnitRepository extends CrudRepository<PendingActionUnitAttribution, PendingActionUnitAttribution.PendingActionUnitId> {
}
