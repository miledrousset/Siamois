package fr.siamois.ui.bean.customform;


import fr.siamois.domain.services.spatialunit.SpatialUnitTreeService;

import javax.faces.bean.ViewScoped;

@ViewScoped
public class SpatialUnitTreeBean {

    private final SpatialUnitTreeService spatialUnitTreeService;

    public SpatialUnitTreeBean(SpatialUnitTreeService spatialUnitTreeService) {
        this.spatialUnitTreeService = spatialUnitTreeService;
    }


}
