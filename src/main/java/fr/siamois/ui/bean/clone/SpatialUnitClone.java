package fr.siamois.ui.bean.clone;


import fr.siamois.domain.models.ark.Ark;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import lombok.Data;
import org.locationtech.jts.geom.MultiPolygon;

@Data
public class SpatialUnitClone {

    private String name;

    private Ark ark;

    private Concept category;

    private MultiPolygon geom;

    private Boolean validated;

    public SpatialUnitClone (SpatialUnit spatialUnit) {
        name = spatialUnit.getName();
        ark = spatialUnit.getArk();
        category = spatialUnit.getCategory();
        geom = spatialUnit.getGeom();
        validated = spatialUnit.getValidated();
    }
}
