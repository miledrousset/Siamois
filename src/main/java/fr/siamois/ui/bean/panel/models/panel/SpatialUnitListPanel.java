package fr.siamois.ui.bean.panel.models.panel;

import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.primefaces.model.menu.DefaultMenuItem;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SpatialUnitListPanel extends AbstractPanel {

    private final transient SpatialUnitService spatialUnitService;
    private final SessionSettingsBean sessionSettingsBean;

    private List<SpatialUnit> spatialUnitList;
    private String spatialUnitListErrorMessage;


    public SpatialUnitListPanel(SpatialUnitService spatialUnitService, SessionSettingsBean sessionSettingsBean) {
        super("Unités géographiques", "bi bi-geo-alt", "siamois-panel spatial-unit-panel spatial-unit-list-panel");
        this.spatialUnitService = spatialUnitService;
        this.sessionSettingsBean = sessionSettingsBean;
    }

    public void init()  {
        try {
            // Add current item to breadcrumb
            DefaultMenuItem item = DefaultMenuItem.builder()
                    .value("Unités géographiques")
                    .icon("bi bi-geo-alt")
                    .build();
            this.getBreadcrumb().getModel().getElements().add(item);
            // Get all the spatial unit within the institution that don't have a parent
            spatialUnitList = spatialUnitService.findAllWithoutParentsOfInstitution(sessionSettingsBean.getSelectedInstitution());
        } catch (RuntimeException e) {
            spatialUnitList = null;
            spatialUnitListErrorMessage = "Failed to load spatial units: " + e.getMessage();
        }
    }


    @Override
    public String display() {
        return "/panel/spatialUnitListPanel.xhtml";
    }

    public static class SpatialUnitListPanelBuilder {

        private final SpatialUnitListPanel spatialUnitListPanel;

        public SpatialUnitListPanelBuilder(ObjectProvider<SpatialUnitListPanel> spatialUnitListPanelProvider) {
            this.spatialUnitListPanel = spatialUnitListPanelProvider.getObject();
        }

        public SpatialUnitListPanel.SpatialUnitListPanelBuilder breadcrumb(PanelBreadcrumb breadcrumb) {
            spatialUnitListPanel.setBreadcrumb(breadcrumb);

            return this;
        }

        public SpatialUnitListPanel build() {
            spatialUnitListPanel.init();
            return spatialUnitListPanel;
        }
    }
}
