package fr.siamois.bean.Home;
import fr.siamois.models.SpatialUnit;
import fr.siamois.services.SpatialUnitService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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

    @PostConstruct
    public void init()  {
        spatialUnitList = spatialUnitService.findAllWithoutParents();
    }



}
