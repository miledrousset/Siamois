package fr.siamois.ui.bean.panel;

import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import fr.siamois.ui.bean.panel.models.panel.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import javax.faces.bean.ApplicationScoped;
import java.util.ArrayList;

@Component
@ApplicationScoped
public class PanelFactory {

    private final ObjectProvider<SpatialUnitListPanel> spatialUnitListPanelProvider;
    private final ObjectProvider<SpatialUnitPanel> spatialUnitPanelProvider;
    private final ObjectProvider<NewSpatialUnitPanel> newSpatialUnitPanelProvider;
    private final ObjectProvider<NewActionUnitPanel> newActionUnitPanelProvider;
    private final ObjectProvider<ActionUnitPanel> actionUnitPanelProvider;
    private final ObjectProvider<NewRecordingUnitPanel> newRecordingUnitPanelProvider;


    public PanelFactory(
            ObjectProvider<SpatialUnitListPanel> spatialUnitListPanelProvider,
            ObjectProvider<SpatialUnitPanel> spatialUnitPanelProvider,
            ObjectProvider<NewSpatialUnitPanel> newSpatialUnitPanelProvider,
            ObjectProvider<NewActionUnitPanel> newActionUnitPanelProvider,
            ObjectProvider<ActionUnitPanel> actionUnitPanelProvider, ObjectProvider<NewRecordingUnitPanel> newRecordingUnitPanelProvider) {

        this.spatialUnitListPanelProvider = spatialUnitListPanelProvider;
        this.spatialUnitPanelProvider = spatialUnitPanelProvider;
        this.newSpatialUnitPanelProvider = newSpatialUnitPanelProvider;
        this.newActionUnitPanelProvider = newActionUnitPanelProvider;
        this.actionUnitPanelProvider = actionUnitPanelProvider;
        this.newRecordingUnitPanelProvider = newRecordingUnitPanelProvider;
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

    public ActionUnitPanel createActionUnitPanel(Long actionUnitId, PanelBreadcrumb currentBreadcrumb) {

        PanelBreadcrumb bc = new PanelBreadcrumb();
        bc.getModel().getElements().clear();
        bc.getModel().getElements().addAll(new ArrayList<>(currentBreadcrumb.getModel().getElements()));

        return new ActionUnitPanel.ActionUnitPanelBuilder(actionUnitPanelProvider)
                .id(actionUnitId)
                .breadcrumb(bc)
                .build();

    }

    public ActionUnitPanel createActionUnitPanel(Long actionUnitId) {

        PanelBreadcrumb bc = new PanelBreadcrumb();

        return new ActionUnitPanel.ActionUnitPanelBuilder(actionUnitPanelProvider)
                .id(actionUnitId)
                .breadcrumb(bc)
                .build();

    }

    public NewSpatialUnitPanel createNewSpatialUnitPanel(PanelBreadcrumb currentBreadcrumb) {

        PanelBreadcrumb bc = new PanelBreadcrumb();
        bc.getModel().getElements().clear();
        bc.getModel().getElements().addAll(new ArrayList<>(currentBreadcrumb.getModel().getElements()));

        return new NewSpatialUnitPanel.NewSpatialUnitPanelBuilder(newSpatialUnitPanelProvider)
                .breadcrumb(bc)
                .build();

    }

    public NewRecordingUnitPanel createNewRecordingUnitPanel(Long actionUnitId, PanelBreadcrumb currentBreadcrumb) {

        PanelBreadcrumb bc = new PanelBreadcrumb();
        bc.getModel().getElements().clear();
        bc.getModel().getElements().addAll(new ArrayList<>(currentBreadcrumb.getModel().getElements()));

        return new NewRecordingUnitPanel.NewRecordingUnitPanelBuilder(newRecordingUnitPanelProvider)
                .breadcrumb(bc)
                .actionUnitId(actionUnitId)
                .build();

    }

    public NewActionUnitPanel createNewActionUnitPanel(Long spatialUnitId, PanelBreadcrumb currentBreadcrumb) {

        PanelBreadcrumb bc = new PanelBreadcrumb();
        bc.getModel().getElements().clear();
        bc.getModel().getElements().addAll(new ArrayList<>(currentBreadcrumb.getModel().getElements()));

        return new NewActionUnitPanel.NewActionUnitPanelBuilder(newActionUnitPanelProvider)
                .breadcrumb(bc)
                .spatialUnitId(spatialUnitId)
                .build();

    }

    public SpatialUnitListPanel createSpatialUnitListPanel() {
        return spatialUnitListPanelProvider.getObject();
    }

}