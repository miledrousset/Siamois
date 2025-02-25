package fr.siamois.infrastructure.repositories;

import fr.siamois.models.ark.Ark;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ArkRepository extends CrudRepository<Ark, Long> {

    @Query(
            nativeQuery = true,
            value = "SELECT a.* FROM ark a WHERE a.fk_institution_id = :institutionId AND lower(a.qualifier) = lower(:qualifier)"
    )
    Optional<Ark> findByInstitutionAndQualifier(Long institutionId, String qualifier);

}
