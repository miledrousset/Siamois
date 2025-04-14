package fr.siamois.infrastructure.database.repositories;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.ark.Ark;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            value = "SELECT su.*, l.* " +
                    "FROM spatial_unit su " +
                    "         LEFT JOIN spatial_hierarchy sh ON su.spatial_unit_id = sh.fk_child_id " +
                    "         LEFT JOIN concept c ON su.fk_concept_category_id = c.concept_id " +
                    "         LEFT JOIN label l ON c.concept_id = l.fk_concept_id " +
                    "WHERE su.fk_institution_id = :institutionId " +
                    "  AND :langCode = l.lang_code "+
                    "  AND (CAST(:name AS TEXT) IS NULL OR LOWER(su.name) LIKE LOWER(CONCAT('%', CAST(:name AS TEXT), '%'))) " +
                    "  AND (CAST(:categoryIds AS BIGINT[]) IS NULL OR su.fk_concept_category_id IN (:categoryIds)) " +
                    "  AND (CAST(:global AS TEXT) IS NULL OR LOWER(su.name) LIKE LOWER(CONCAT('%', CAST(:global AS TEXT), '%')))",
            countQuery = "SELECT count(su.*) " +
                    "FROM spatial_unit su " +
                    "         LEFT JOIN spatial_hierarchy sh ON su.spatial_unit_id = sh.fk_child_id " +
                    "         LEFT JOIN concept c ON su.fk_concept_category_id = c.concept_id " +
                    "WHERE su.fk_institution_id = :institutionId " +
                    "  AND sh.fk_parent_id IS NULL " +
                    "  AND (CAST(:name AS TEXT) IS NULL OR LOWER(su.name) LIKE LOWER(CONCAT('%', CAST(:name AS TEXT), '%'))) " +
                    "  AND (CAST(:categoryIds AS BIGINT[]) IS NULL OR su.fk_concept_category_id IN (:categoryIds)) " +
                    "  AND (CAST(:global AS TEXT) IS NULL OR LOWER(su.name) LIKE LOWER(CONCAT('%', CAST(:global AS TEXT), '%')))"
    )
    Page<SpatialUnit> findAllByInstitutionAndByNameContainingAndByCategoriesAndByGlobalContaining(@Param("institutionId") Long institutionId,
                                      @Param("name") String name,
                                      @Param("categoryIds") Long[] categoryIds,
                                      @Param("global") String global,
                                      Pageable pageable);

    @Query(
            nativeQuery = true,
            value = "SELECT su.*" +
                    "FROM spatial_unit su " +
                    "         LEFT JOIN spatial_hierarchy sh ON su.spatial_unit_id = sh.fk_child_id " +
                    "         LEFT JOIN concept c ON su.fk_concept_category_id = c.concept_id " +
                    "         JOIN spatial_hierarchy suh ON su.spatial_unit_id = suh.fk_child_id "+
                    "WHERE suh.fk_parent_id = :parentId " +
                    "  AND (CAST(:name AS TEXT) IS NULL OR LOWER(su.name) LIKE LOWER(CONCAT('%', CAST(:name AS TEXT), '%'))) " +
                    "  AND (CAST(:categoryIds AS BIGINT[]) IS NULL OR su.fk_concept_category_id IN (:categoryIds)) " +
                    "  AND (CAST(:global AS TEXT) IS NULL OR LOWER(su.name) LIKE LOWER(CONCAT('%', CAST(:global AS TEXT), '%')))",
            countQuery = "SELECT count(su.*) " +
                    "FROM spatial_unit su " +
                    "         LEFT JOIN spatial_hierarchy sh ON su.spatial_unit_id = sh.fk_child_id " +
                    "         LEFT JOIN concept c ON su.fk_concept_category_id = c.concept_id " +
                    "         JOIN spatial_hierarchy suh ON su.spatial_unit_id = suh.fk_child_id "+
                    "WHERE suh.fk_parent_id = :parentId " +
                    "  AND (CAST(:name AS TEXT) IS NULL OR LOWER(su.name) LIKE LOWER(CONCAT('%', CAST(:name AS TEXT), '%'))) " +
                    "  AND (CAST(:categoryIds AS BIGINT[]) IS NULL OR su.fk_concept_category_id IN (:categoryIds)) " +
                    "  AND (CAST(:global AS TEXT) IS NULL OR LOWER(su.name) LIKE LOWER(CONCAT('%', CAST(:global AS TEXT), '%')))"
    )
    Page<SpatialUnit> findAllByParentAndByNameContainingAndByCategoriesAndByGlobalContaining(@Param("parentId") Long parentId,
                                                                                                  @Param("name") String name,
                                                                                                  @Param("categoryIds") Long[] categoryIds,
                                                                                                  @Param("global") String global,
                                                                                                  Pageable pageable);

    @Query(
            nativeQuery = true,
            value = "SELECT su.*" +
                    "FROM spatial_unit su " +
                    "         LEFT JOIN spatial_hierarchy sh ON su.spatial_unit_id = sh.fk_child_id " +
                    "         LEFT JOIN concept c ON su.fk_concept_category_id = c.concept_id " +
                    "         JOIN spatial_hierarchy suh ON su.spatial_unit_id = suh.fk_parent_id "+
                    "WHERE suh.fk_child_id = :childId " +
                    "  AND (CAST(:name AS TEXT) IS NULL OR LOWER(su.name) LIKE LOWER(CONCAT('%', CAST(:name AS TEXT), '%'))) " +
                    "  AND (CAST(:categoryIds AS BIGINT[]) IS NULL OR su.fk_concept_category_id IN (:categoryIds)) " +
                    "  AND (CAST(:global AS TEXT) IS NULL OR LOWER(su.name) LIKE LOWER(CONCAT('%', CAST(:global AS TEXT), '%')))",
            countQuery = "SELECT count(su.*) " +
                    "FROM spatial_unit su " +
                    "         LEFT JOIN spatial_hierarchy sh ON su.spatial_unit_id = sh.fk_child_id " +
                    "         LEFT JOIN concept c ON su.fk_concept_category_id = c.concept_id " +
                    "         JOIN spatial_hierarchy suh ON su.spatial_unit_id = suh.fk_parent_id "+
                    "WHERE suh.fk_child_id = :childId " +
                    "  AND (CAST(:name AS TEXT) IS NULL OR LOWER(su.name) LIKE LOWER(CONCAT('%', CAST(:name AS TEXT), '%'))) " +
                    "  AND (CAST(:categoryIds AS BIGINT[]) IS NULL OR su.fk_concept_category_id IN (:categoryIds)) " +
                    "  AND (CAST(:global AS TEXT) IS NULL OR LOWER(su.name) LIKE LOWER(CONCAT('%', CAST(:global AS TEXT), '%')))"
    )
    Page<SpatialUnit> findAllByChildAndByNameContainingAndByCategoriesAndByGlobalContaining(@Param("childId") Long childId,
                                                                                             @Param("name") String name,
                                                                                             @Param("categoryIds") Long[] categoryIds,
                                                                                             @Param("global") String global,
                                                                                             Pageable pageable);

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

    long countByCreatedByInstitution(Institution institution);
}

