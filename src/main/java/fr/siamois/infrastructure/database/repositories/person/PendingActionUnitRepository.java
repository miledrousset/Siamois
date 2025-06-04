package fr.siamois.infrastructure.database.repositories.person;

import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.auth.pending.PendingActionUnitAttribution;
import fr.siamois.domain.models.auth.pending.PendingInstitutionInvite;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PendingActionUnitRepository extends CrudRepository<PendingActionUnitAttribution, PendingActionUnitAttribution.PendingActionUnitId> {
    Optional<PendingActionUnitAttribution> findByActionUnitAndInstitutionInvite(ActionUnit actionUnit, PendingInstitutionInvite institutionInvite);

    Set<PendingActionUnitAttribution> findByInstitutionInvite(PendingInstitutionInvite institutionInvite);
}
