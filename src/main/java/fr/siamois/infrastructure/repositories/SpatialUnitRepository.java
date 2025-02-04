package fr.siamois.infrastructure.repositories;

import fr.siamois.infrastructure.repositories.history.TraceableEntries;
import fr.siamois.models.SpatialUnit;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface SpatialUnitRepository extends CrudRepository<SpatialUnit, Long>, TraceableEntries {

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

    @Transactional
    @Modifying
    @Query(
            nativeQuery = true,
            value = "INSERT INTO spatial_hierarchy(fk_parent_id, fk_child_id) " +
                    "VALUES (:parentSpatialUnitId, :childSpatialUnitId)"
    )
    void saveSpatialUnitHierarchy(Long parentSpatialUnitId, Long childSpatialUnitId);

    @Query(
            nativeQuery = true,
            value = "SELECT su.* FROM spatial_unit su WHERE fk_author_id = :author AND creation_time BETWEEN :start AND :end"
    )
    List<SpatialUnit> findAllCreatedBetweenByUser(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end, @Param("author") Long personId);

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
}

