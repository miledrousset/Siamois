package fr.siamois.infrastructure.database.repositories.actionunit;

import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.ark.Ark;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActionUnitRepository extends CrudRepository<ActionUnit, Long> {

    List<ActionUnit> findAllBySpatialUnitId(Long id);

    Optional<ActionUnit> findByArk(Ark ark);

    List<ActionUnit> findAllByArkIsNullAndCreatedByInstitution(@NotNull Institution createdByInstitution);

    long countByCreatedByInstitution(Institution institution);

    @Query(
            nativeQuery = true,
            value = "SELECT " +
                    "    au.*" +
                    "FROM action_unit au " +
                    "WHERE au.fk_institution_id = :institutionId ",
            countQuery = "SELECT count(au.*) " +
                    "FROM action_unit au " +
                    "WHERE au.fk_institution_id = :institutionId "
    )
    Page<ActionUnit> findAllByInstitution(@Param("institutionId") Long institutionId, Pageable pageable);
}
