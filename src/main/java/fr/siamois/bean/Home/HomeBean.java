package fr.siamois.bean.Home;

import fr.siamois.bean.SessionSettings;
import fr.siamois.models.spatialunit.SpatialUnit;
import fr.siamois.models.auth.Person;
import fr.siamois.models.events.InstitutionChangeEvent;
import fr.siamois.services.SpatialUnitService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.List;

/**
 * <p>This bean handles the home page</p>
 * <p>It is used to display the list of spatial units without parents</p>
 *
 * @author Grégory Bliault
 */
@Slf4j
@Component
@SessionScoped
public class HomeBean implements Serializable {

    private final SpatialUnitService spatialUnitService;
    private final SessionSettings sessionSettings;

    @Getter
    private List<SpatialUnit> spatialUnitList;

    @Getter private String spatialUnitListErrorMessage;

    public HomeBean(SpatialUnitService spatialUnitService, SessionSettings sessionSettings) {
        this.spatialUnitService = spatialUnitService;
        this.sessionSettings = sessionSettings;
    }

    public void init()  {
        try {
            Person author = sessionSettings.getAuthenticatedUser();
            if (author.hasRole("ADMIN")) {
                spatialUnitList = spatialUnitService.findAllWithoutParents();
            } else {
                spatialUnitList = spatialUnitService.findAllWithoutParentsOfInstitution(sessionSettings.getSelectedInstitution());
            }
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            spatialUnitList = null;
            spatialUnitListErrorMessage = "Failed to load spatial units: " + e.getMessage();
        }
    }

    @EventListener(InstitutionChangeEvent.class)
    public void onInstitutionChangeEvent() {
        log.trace("InstitutionChangeEvent received. Updating teams");
        spatialUnitList = spatialUnitService.findAllWithoutParentsOfInstitution(sessionSettings.getSelectedInstitution());
        spatialUnitListErrorMessage = null;
    }
}
