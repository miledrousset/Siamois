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

    @Query(
            nativeQuery = true,
            value = "SELECT a.* FROM ark a " +
                    "JOIN institution_settings iset ON a.fk_institution_id = iset.fk_institution_id " +
                    "WHERE UPPER(iset.ark_naan) = UPPER(:naan) AND " +
                    "UPPER(a.qualifier) = UPPER(:qualifier) AND " +
                    "iset.ark_is_enabled = TRUE"
    )
    Optional<Ark> findByNaanAndQualifier(String naan, String qualifier);
}
