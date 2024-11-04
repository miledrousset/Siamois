package fr.siamois.repositories;

import fr.siamois.models.SpatialUnit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpatialUnitRepository extends CrudRepository<SpatialUnit, Integer> {

    @Query(
            nativeQuery = true,
            value = "SELECT * FROM spatial_unit su JOIN spatial_hierarchy suh ON su.spatial_unit_id = suh.fk_child_id WHERE suh.fk_parent_id = :spatialUnit"
    )
    List<SpatialUnit> findAllChildOfSpatialUnit(@Param("spatialUnit") SpatialUnit spatialUnit);

    @Query(
            nativeQuery = true,
            value = "SELECT * FROM spatial_unit su JOIN spatial_hierarchy suh ON su.spatial_unit_id = suh.fk_parent_id WHERE suh.fk_child_id = :spatialUnit"
    )
    List<SpatialUnit> findAllParentsOfSpatialUnit(@Param("spatialUnit") SpatialUnit spatialUnit);

}

