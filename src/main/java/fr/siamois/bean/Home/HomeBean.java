package fr.siamois.bean.Home;

import fr.siamois.models.SpatialUnit;
import fr.siamois.services.SpatialUnitService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.bean.SessionScoped;
import java.io.IOException;
import java.util.List;

@Component
@SessionScoped
public class HomeBean {

    @Autowired
    private SpatialUnitService spatialUnitService;

    @Getter
    private List<SpatialUnit> spatialUnitList;

    @Getter private String spatialUnitListErrorMessage;

    @PostConstruct
    public void init()  {
        try {
            // Initializing the bean: we need all the spatial units without parents
            spatialUnitList = spatialUnitService.findAllWithoutParents();
        } catch (RuntimeException e) {
            spatialUnitListErrorMessage = "Failed to load spatial units: " + e.getMessage();
        }
    }
}
