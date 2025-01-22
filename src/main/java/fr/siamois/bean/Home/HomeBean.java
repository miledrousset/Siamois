package fr.siamois.bean.Home;

import fr.siamois.bean.ObserverBean;
import fr.siamois.bean.SessionSettings;
import fr.siamois.models.SpatialUnit;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.NoTeamSelectedException;
import fr.siamois.services.SpatialUnitService;
import fr.siamois.services.Subscriber;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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
public class HomeBean implements Serializable, Subscriber {

    private final SpatialUnitService spatialUnitService;
    private final SessionSettings sessionSettings;

    @Getter
    private List<SpatialUnit> spatialUnitList;

    @Getter private String spatialUnitListErrorMessage;

    public HomeBean(SpatialUnitService spatialUnitService, SessionSettings sessionSettings, ObserverBean observerBean) {
        this.spatialUnitService = spatialUnitService;
        this.sessionSettings = sessionSettings;
        observerBean.subscribeToSignal(this, "teamChange");
    }

    public void init()  {
        try {
            Person author = sessionSettings.getAuthenticatedUser();
            if (author.hasRole("ADMIN")) {
                spatialUnitList = spatialUnitService.findAllWithoutParents();
            } else {
                spatialUnitList = spatialUnitService.findAllWithoutParentsOfTeam(sessionSettings.getSelectedTeam());
            }
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            spatialUnitList = null;
            spatialUnitListErrorMessage = "Failed to load spatial units: " + e.getMessage();
        }
    }

    @Override
    public void onSignal(String signal) {
        log.trace("Signal received : {}. Updating teams", signal);
        if (signal.equalsIgnoreCase("teamChange")) {
            try {
                spatialUnitList = spatialUnitService.findAllWithoutParentsOfTeam(sessionSettings.getSelectedTeam());
                spatialUnitListErrorMessage = null;
            } catch (NoTeamSelectedException e) {
                log.error("Failed to load teams", e);
                spatialUnitList = null;
                spatialUnitListErrorMessage = "Failed to load team";
            }
        }
    }
}
