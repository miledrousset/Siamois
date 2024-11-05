package fr.siamois.bean.Home;

import fr.siamois.models.SpatialUnit;
import org.springframework.stereotype.Component;


import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@Component
@ViewScoped
public class NavigationBean {
    public String goToSpatialUnitPage(SpatialUnit spatialUnit) {
        return "/pages/spatialUnit/spatialUnit.xhtml?faces-redirect=true&id=" + spatialUnit.getId();
    }
}
