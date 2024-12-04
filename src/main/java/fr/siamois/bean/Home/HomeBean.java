package fr.siamois.bean.Home;

import fr.siamois.models.SpatialUnit;
import fr.siamois.services.SpatialUnitService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.List;

@Component
@SessionScoped
public class HomeBean implements Serializable {

    private final SpatialUnitService spatialUnitService;

    @Getter
    private List<SpatialUnit> spatialUnitList;

    @Getter private String spatialUnitListErrorMessage;

    public HomeBean(SpatialUnitService spatialUnitService) {
        this.spatialUnitService = spatialUnitService;
    }

    @PostConstruct
    public void init()  {
        try {
            // Initializing the bean: we need all the spatial units without parents
            spatialUnitList = spatialUnitService.findAllWithoutParents();
        } catch (RuntimeException e) {
            spatialUnitList = null;
            spatialUnitListErrorMessage = "Failed to load spatial units: " + e.getMessage();
        }
    }
}
