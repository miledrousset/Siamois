package fr.siamois.ui.bean.panel;

import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import fr.siamois.ui.bean.panel.models.panel.SpatialUnitListPanel;
import fr.siamois.ui.bean.panel.models.panel.SpatialUnitPanel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import javax.faces.bean.ApplicationScoped;
import java.util.ArrayList;

@Component
@ApplicationScoped
public class PanelFactory {

    private final ObjectProvider<SpatialUnitListPanel> spatialUnitListPanelProvider;
    private final ObjectProvider<SpatialUnitPanel> spatialUnitPanelProvider;


    public PanelFactory(
            ObjectProvider<SpatialUnitListPanel> spatialUnitListPanelProvider,
            ObjectProvider<SpatialUnitPanel> spatialUnitPanelProvider) {

        this.spatialUnitListPanelProvider = spatialUnitListPanelProvider;
        this.spatialUnitPanelProvider = spatialUnitPanelProvider;
    }

    public SpatialUnitPanel createSpatialUnitPanel(Long spatialUnitId, PanelBreadcrumb currentBreadcrumb) {

        PanelBreadcrumb bc = new PanelBreadcrumb();
        bc.getModel().getElements().clear();
        bc.getModel().getElements().addAll(new ArrayList<>(currentBreadcrumb.getModel().getElements()));

        return new SpatialUnitPanel.SpatialUnitPanelBuilder(spatialUnitPanelProvider)
                .id(spatialUnitId)
                .breadcrumb(bc)
                .build();

    }

    public SpatialUnitListPanel createSpatialUnitListPanel() {
        return spatialUnitListPanelProvider.getObject();
    }

}