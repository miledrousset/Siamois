package fr.siamois.bean.Home;

import fr.siamois.bean.SessionSettings;
import fr.siamois.models.SpatialUnit;
import fr.siamois.services.SpatialUnitService;
import fr.siamois.utils.AuthenticatedUserUtils;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.util.List;

@Component
@SessionScoped
public class HomeBean {

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
