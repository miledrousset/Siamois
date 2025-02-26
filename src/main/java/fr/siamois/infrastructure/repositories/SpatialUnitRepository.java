package fr.siamois.infrastructure.repositories;

import fr.siamois.models.Institution;
import fr.siamois.models.ark.Ark;
import fr.siamois.models.spatialunit.SpatialUnit;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpatialUnitRepository extends CrudRepository<SpatialUnit, Long> {

    @Query(
            nativeQuery = true,
            value = "SELECT su.* FROM spatial_unit su JOIN spatial_hierarchy suh ON su.spatial_unit_id = suh.fk_child_id WHERE suh.fk_parent_id = :spatialUnitId"
    )
    List<SpatialUnit> findAllChildOfSpatialUnit(@Param("spatialUnitId") Long spatialUnitId);

    @Query(
            nativeQuery = true,
            value = "SELECT su.* FROM spatial_unit su JOIN spatial_hierarchy suh ON su.spatial_unit_id = suh.fk_parent_id WHERE suh.fk_child_id = :spatialUnitId"
    )
    List<SpatialUnit> findAllParentsOfSpatialUnit(@Param("spatialUnitId") Long spatialUnitId);


    @Query(
        nativeQuery = true,
        value = "SELECT su.* " +
        "FROM spatial_unit su LEFT JOIN spatial_hierarchy sh " +
        "ON su.spatial_unit_id = sh.fk_child_id " +
        "WHERE sh.fk_parent_id IS NULL;"
    )
    List<SpatialUnit> findAllWithoutParents();

    @Query(
            nativeQuery = true,
            value = "SELECT su.* " +
                    "FROM spatial_unit su " +
                    "         LEFT JOIN spatial_hierarchy sh ON su.spatial_unit_id = sh.fk_child_id " +
                    "WHERE su.fk_institution_id = :institutionId " +
                    "  AND sh.fk_parent_id IS NULL"
    )
    List<SpatialUnit> findAllWithoutParentsOfInstitution(Long institutionId);

    @Query(
            nativeQuery = true,
            value = "SELECT su.* FROM spatial_unit su WHERE su.fk_institution_id = :institutionId"
    )
    List<SpatialUnit> findAllOfInstitution(Long institutionId);

    @Transactional
    @Modifying
    @Query(
            nativeQuery = true,
            value = "INSERT INTO spatial_hierarchy(fk_parent_id, fk_child_id) " +
                    "VALUES (:parentId, :childId)"
    )
    void addParentToSpatialUnit(Long childId, Long parentId);

    @Query(
            nativeQuery = true,
            value = "SELECT su.* FROM spatial_unit su " +
                    "WHERE UPPER(su.name) = UPPER(:spatialUnitName) AND su.fk_institution_id = :institutionId"
    )
    Optional<SpatialUnit> findByNameAndInstitution(String spatialUnitName, Long institutionId);

    Optional<SpatialUnit> findByArk(@NotNull Ark ark);

    List<SpatialUnit> findAllByArkIsNullAndCreatedByInstitution(@NotNull Institution createdByInstitution);
}

