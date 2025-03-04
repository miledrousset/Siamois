package fr.siamois.ui.bean.panel.models;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.ui.bean.SessionSettingsBean;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SpatialUnitListPanel extends AbstractPanel {

    private final SpatialUnitService spatialUnitService;
    private final SessionSettingsBean sessionSettingsBean;

    private List<SpatialUnit> spatialUnitList;
    private String spatialUnitListErrorMessage;

    public SpatialUnitListPanel(SpatialUnitService spatialUnitService, SessionSettingsBean sessionSettingsBean) {
        this.spatialUnitService = spatialUnitService;
        this.sessionSettingsBean = sessionSettingsBean;
        init();
    }

    public void init()  {
        try {
            Person author = sessionSettingsBean.getAuthenticatedUser();
            if (author.isSuperAdmin()) {
                spatialUnitList = spatialUnitService.findAllWithoutParents();
            } else {
                spatialUnitList = spatialUnitService.findAllWithoutParentsOfInstitution(sessionSettingsBean.getSelectedInstitution());
            }
        } catch (RuntimeException e) {
            spatialUnitList = null;
            spatialUnitListErrorMessage = "Failed to load spatial units: " + e.getMessage();
        }
    }

    @Override
    public String display() {
        return "/panel/spatialUnitListPanel.xhtml";
    }
}
