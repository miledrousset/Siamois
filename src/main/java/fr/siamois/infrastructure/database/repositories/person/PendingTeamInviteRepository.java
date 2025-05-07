package fr.siamois.infrastructure.database.repositories.person;

import fr.siamois.domain.models.auth.pending.PendingInstitutionInvite;
import fr.siamois.domain.models.auth.pending.PendingTeamInvite;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PendingTeamInviteRepository extends CrudRepository<PendingTeamInvite, Long> {

    Optional<PendingTeamInvite> findByPendingInstitutionInvite(PendingInstitutionInvite pendingInstitutionInvite);
}
