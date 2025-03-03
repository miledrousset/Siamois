package fr.siamois.ui.bean.home;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.events.InstitutionChangeEvent;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.ui.bean.SessionSettingsBean;
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

    private final transient SpatialUnitService spatialUnitService;
    private final SessionSettingsBean sessionSettingsBean;

    @Getter
    private transient List<SpatialUnit> spatialUnitList;

    @Getter private String spatialUnitListErrorMessage;

    public HomeBean(SpatialUnitService spatialUnitService, SessionSettingsBean sessionSettingsBean) {
        this.spatialUnitService = spatialUnitService;
        this.sessionSettingsBean = sessionSettingsBean;
    }

    public void init()  {
        try {
            Person author = sessionSettingsBean.getAuthenticatedUser();
            if (author.hasRole("ADMIN")) {
                spatialUnitList = spatialUnitService.findAllWithoutParents();
            } else {
                spatialUnitList = spatialUnitService.findAllWithoutParentsOfInstitution(sessionSettingsBean.getSelectedInstitution());
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
        spatialUnitList = spatialUnitService.findAllWithoutParentsOfInstitution(sessionSettingsBean.getSelectedInstitution());
        spatialUnitListErrorMessage = null;
    }
}
