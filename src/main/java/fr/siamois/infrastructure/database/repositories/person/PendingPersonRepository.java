package fr.siamois.infrastructure.database.repositories.person;

import fr.siamois.domain.models.auth.pending.PendingPerson;
import jakarta.validation.constraints.Email;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PendingPersonRepository extends CrudRepository<PendingPerson, Long> {
    boolean existsByRegisterToken(String registerToken);

    Optional<PendingPerson> findByRegisterToken(String registerToken);

    Optional<PendingPerson> findByEmail(@Email String email);
}
