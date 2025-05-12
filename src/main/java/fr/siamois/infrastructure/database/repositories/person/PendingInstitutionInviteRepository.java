package fr.siamois.infrastructure.database.repositories.person;

import fr.siamois.domain.models.auth.pending.PendingInstitutionInvite;
import fr.siamois.domain.models.auth.pending.PendingPerson;
import fr.siamois.domain.models.institution.Institution;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface PendingInstitutionInviteRepository extends CrudRepository<PendingInstitutionInvite, Long> {
    Optional<PendingInstitutionInvite> findByInstitutionAndPendingPerson(Institution institution, PendingPerson pendingPerson);

    Set<PendingInstitutionInvite> findAllByPendingPerson(PendingPerson pendingPerson);
}
