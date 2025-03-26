package fr.siamois.ui.bean.panel.models.panel;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Component
@Scope("prototype")
public class SpatialUnitListPanel extends AbstractPanel {

    private final SpatialUnitService spatialUnitService;
    private final SessionSettingsBean sessionSettingsBean;

    private List<SpatialUnit> spatialUnitList;
    private String spatialUnitListErrorMessage;


    public SpatialUnitListPanel(SpatialUnitService spatialUnitService, SessionSettingsBean sessionSettingsBean) {
        super("spatial-unit-list", "Unités géographiques", "welcome-panel", "bi bi-geo-alt");
        this.spatialUnitService = spatialUnitService;
        this.sessionSettingsBean = sessionSettingsBean;
        this.setBreadcrumb(new PanelBreadcrumb());
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
