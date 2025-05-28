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
    private final ObjectProvider<ActionUnitListPanel> actionUnitListPanelProvider;
    private final ObjectProvider<SpatialUnitPanel> spatialUnitPanelProvider;
    private final ObjectProvider<NewSpatialUnitPanel> newSpatialUnitPanelProvider;
    private final ObjectProvider<NewActionUnitPanel> newActionUnitPanelProvider;
    private final ObjectProvider<ActionUnitPanel> actionUnitPanelProvider;
    private final ObjectProvider<NewRecordingUnitPanel> newRecordingUnitPanelProvider;
    private final ObjectProvider<RecordingUnitPanel> recordingUnitPanelProvider;
    private final ObjectProvider<WelcomePanel> welcomePanelProvider;


    public PanelFactory(
            ObjectProvider<SpatialUnitListPanel> spatialUnitListPanelProvider,
            ObjectProvider<ActionUnitListPanel> actionUnitListPanelProvider,
            ObjectProvider<SpatialUnitPanel> spatialUnitPanelProvider,
            ObjectProvider<NewSpatialUnitPanel> newSpatialUnitPanelProvider,
            ObjectProvider<NewActionUnitPanel> newActionUnitPanelProvider,
            ObjectProvider<ActionUnitPanel> actionUnitPanelProvider,
            ObjectProvider<NewRecordingUnitPanel> newRecordingUnitPanelProvider,
            ObjectProvider<RecordingUnitPanel> recordingUnitPanelProvider,
            ObjectProvider<WelcomePanel> welcomePanelProvider) {

        this.spatialUnitListPanelProvider = spatialUnitListPanelProvider;
        this.actionUnitListPanelProvider = actionUnitListPanelProvider;
        this.spatialUnitPanelProvider = spatialUnitPanelProvider;
        this.newSpatialUnitPanelProvider = newSpatialUnitPanelProvider;
        this.newActionUnitPanelProvider = newActionUnitPanelProvider;
        this.actionUnitPanelProvider = actionUnitPanelProvider;
        this.newRecordingUnitPanelProvider = newRecordingUnitPanelProvider;
        this.recordingUnitPanelProvider = recordingUnitPanelProvider;
        this.welcomePanelProvider = welcomePanelProvider;
    }

    public SpatialUnitPanel createSpatialUnitPanel(Long spatialUnitId) {

        PanelBreadcrumb bc = new PanelBreadcrumb();

        return new SpatialUnitPanel.SpatialUnitPanelBuilder(spatialUnitPanelProvider)
                .id(spatialUnitId)
                .breadcrumb(bc)
                .build();

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

        PanelBreadcrumb bc = null;

        if (currentBreadcrumb != null) {
            bc = new PanelBreadcrumb();
            bc.getModel().getElements().clear();
            bc.getModel().getElements().addAll(new ArrayList<>(currentBreadcrumb.getModel().getElements()));
        }

        return new NewSpatialUnitPanel.NewSpatialUnitPanelBuilder(newSpatialUnitPanelProvider)
                .breadcrumb(bc)
                .build();

    }

    public RecordingUnitPanel createRecordingUnitPanel(Long recordingUnitId, PanelBreadcrumb currentBreadcrumb) {

        PanelBreadcrumb bc = new PanelBreadcrumb();
        bc.getModel().getElements().clear();
        bc.getModel().getElements().addAll(new ArrayList<>(currentBreadcrumb.getModel().getElements()));

        return new RecordingUnitPanel.RecordingUnitPanelBuilder(recordingUnitPanelProvider)
                .id(recordingUnitId)
                .breadcrumb(bc)
                .build();

    }

    public RecordingUnitPanel createRecordingUnitPanel(Long recordingUnitId) {

        PanelBreadcrumb bc = new PanelBreadcrumb();

        return new RecordingUnitPanel.RecordingUnitPanelBuilder(recordingUnitPanelProvider)
                .id(recordingUnitId)
                .breadcrumb(bc)
                .build();

    }

    public NewRecordingUnitPanel createNewRecordingUnitPanel(Long actionUnitId, PanelBreadcrumb currentBreadcrumb) {

        PanelBreadcrumb bc = null;

        if (currentBreadcrumb != null) {
            bc = new PanelBreadcrumb();
            bc.getModel().getElements().clear();
            bc.getModel().getElements().addAll(new ArrayList<>(currentBreadcrumb.getModel().getElements()));
        }

        return new NewRecordingUnitPanel.NewRecordingUnitPanelBuilder(newRecordingUnitPanelProvider)
                .breadcrumb(bc)
                .actionUnitId(actionUnitId)
                .build();

    }

    public NewActionUnitPanel createNewActionUnitPanel(Long spatialUnitId, PanelBreadcrumb currentBreadcrumb) {
        PanelBreadcrumb bc = null;
        if (currentBreadcrumb != null) {
            bc = new PanelBreadcrumb();
            bc.getModel().getElements().clear();
            bc.getModel().getElements().addAll(new ArrayList<>(currentBreadcrumb.getModel().getElements()));
        }

        return new NewActionUnitPanel.NewActionUnitPanelBuilder(newActionUnitPanelProvider)
                .breadcrumb(bc)
                .spatialUnitId(spatialUnitId)
                .build();

    }

    public SpatialUnitListPanel createSpatialUnitListPanel(PanelBreadcrumb currentBreadcrumb) {
        return new SpatialUnitListPanel.SpatialUnitListPanelBuilder(spatialUnitListPanelProvider)
                .breadcrumb(currentBreadcrumb)
                .build();
    }

    public ActionUnitListPanel createActionUnitListPanel(PanelBreadcrumb currentBreadcrumb) {
        return new ActionUnitListPanel.ActionUnitListPanelBuilder(actionUnitListPanelProvider)
                .breadcrumb(currentBreadcrumb)
                .build();
    }

    public WelcomePanel createWelcomePanel() {
        WelcomePanel wp = welcomePanelProvider.getObject();
        wp.setBreadcrumb(new PanelBreadcrumb());
        return wp;
    }

}