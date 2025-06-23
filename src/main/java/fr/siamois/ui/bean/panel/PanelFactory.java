package fr.siamois.ui.bean.panel;

import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import fr.siamois.ui.bean.panel.models.panel.*;
import fr.siamois.ui.bean.panel.models.panel.list.ActionUnitListPanel;
import fr.siamois.ui.bean.panel.models.panel.list.RecordingUnitListPanel;
import fr.siamois.ui.bean.panel.models.panel.list.SpatialUnitListPanel;
import fr.siamois.ui.bean.panel.models.panel.list.SpecimenListPanel;
import fr.siamois.ui.bean.panel.models.panel.single.ActionUnitPanel;
import fr.siamois.ui.bean.panel.models.panel.single.RecordingUnitPanel;
import fr.siamois.ui.bean.panel.models.panel.single.SpatialUnitPanel;
import fr.siamois.ui.bean.panel.models.panel.single.SpecimenPanel;
import fr.siamois.ui.lazydatamodel.BaseSpatialUnitLazyDataModel;
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
    private final ObjectProvider<RecordingUnitPanel> recordingUnitPanelProvider;
    private final ObjectProvider<RecordingUnitListPanel> recordingUnitListPanelProvider;
    private final ObjectProvider<SpecimenListPanel> specimenListPanel;
    private final ObjectProvider<WelcomePanel> welcomePanelProvider;
    private final ObjectProvider<SpecimenPanel> specimenPanelProvider;


    public PanelFactory(
            ObjectProvider<SpatialUnitListPanel> spatialUnitListPanelProvider,
            ObjectProvider<ActionUnitListPanel> actionUnitListPanelProvider,
            ObjectProvider<SpatialUnitPanel> spatialUnitPanelProvider,
            ObjectProvider<NewSpatialUnitPanel> newSpatialUnitPanelProvider,
            ObjectProvider<NewActionUnitPanel> newActionUnitPanelProvider,
            ObjectProvider<ActionUnitPanel> actionUnitPanelProvider,
            ObjectProvider<RecordingUnitPanel> recordingUnitPanelProvider,
            ObjectProvider<RecordingUnitListPanel> recordingUnitListPanelProvider, ObjectProvider<SpecimenListPanel> specimenListPanel,
            ObjectProvider<WelcomePanel> welcomePanelProvider, ObjectProvider<SpecimenPanel> specimenPanelProvider) {

        this.spatialUnitListPanelProvider = spatialUnitListPanelProvider;
        this.actionUnitListPanelProvider = actionUnitListPanelProvider;
        this.spatialUnitPanelProvider = spatialUnitPanelProvider;
        this.newSpatialUnitPanelProvider = newSpatialUnitPanelProvider;
        this.newActionUnitPanelProvider = newActionUnitPanelProvider;
        this.actionUnitPanelProvider = actionUnitPanelProvider;
        this.recordingUnitPanelProvider = recordingUnitPanelProvider;
        this.recordingUnitListPanelProvider = recordingUnitListPanelProvider;
        this.specimenListPanel = specimenListPanel;
        this.welcomePanelProvider = welcomePanelProvider;
        this.specimenPanelProvider = specimenPanelProvider;
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

        SpatialUnitPanel panel = new SpatialUnitPanel.SpatialUnitPanelBuilder(spatialUnitPanelProvider)
                .id(spatialUnitId)
                .breadcrumb(bc)
                .build();

        panel.init();

        return panel;
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

    public NewSpatialUnitPanel createNewSpatialUnitPanel(PanelBreadcrumb currentBreadcrumb, BaseSpatialUnitLazyDataModel lazyModel) {

        PanelBreadcrumb bc = null;

        if (currentBreadcrumb != null) {
            bc = new PanelBreadcrumb();
            bc.getModel().getElements().clear();
            bc.getModel().getElements().addAll(new ArrayList<>(currentBreadcrumb.getModel().getElements()));
        }

        return new NewSpatialUnitPanel.NewSpatialUnitPanelBuilder(newSpatialUnitPanelProvider)
                .breadcrumb(bc)
                .lazyModel(lazyModel)
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

    public SpecimenPanel createSpecimenPanel(Long id, PanelBreadcrumb currentBreadcrumb) {

        PanelBreadcrumb bc = new PanelBreadcrumb();
        bc.getModel().getElements().clear();
        bc.getModel().getElements().addAll(new ArrayList<>(currentBreadcrumb.getModel().getElements()));

        return new SpecimenPanel.Builder(specimenPanelProvider)
                .id(id)
                .breadcrumb(bc)
                .build();

    }

    public SpecimenPanel createSpecimenPanel(Long id) {

        PanelBreadcrumb bc = new PanelBreadcrumb();

        return new SpecimenPanel.Builder(specimenPanelProvider)
                .id(id)
                .breadcrumb(bc)
                .build();

    }

    public NewActionUnitPanel createNewActionUnitPanel(PanelBreadcrumb currentBreadcrumb) {
        PanelBreadcrumb bc = null;
        if (currentBreadcrumb != null) {
            bc = new PanelBreadcrumb();
            bc.getModel().getElements().clear();
            bc.getModel().getElements().addAll(new ArrayList<>(currentBreadcrumb.getModel().getElements()));
        }

        return new NewActionUnitPanel.NewActionUnitPanelBuilder(newActionUnitPanelProvider)
                .breadcrumb(bc)
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

    public NewActionUnitPanel createNewActionUnitPanel(Long spatialUnitId, PanelBreadcrumb currentBreadcrumb, BaseSpatialUnitLazyDataModel lazyModel) {
        PanelBreadcrumb bc = null;
        if (currentBreadcrumb != null) {
            bc = new PanelBreadcrumb();
            bc.getModel().getElements().clear();
            bc.getModel().getElements().addAll(new ArrayList<>(currentBreadcrumb.getModel().getElements()));
        }

        return new NewActionUnitPanel.NewActionUnitPanelBuilder(newActionUnitPanelProvider)
                .breadcrumb(bc)
                .lazyModel(lazyModel)
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

    public RecordingUnitListPanel createRecordingUnitListPanel(PanelBreadcrumb currentBreadcrumb) {
        return new RecordingUnitListPanel.RecordingUnitListPanelBuilder(recordingUnitListPanelProvider)
                .breadcrumb(currentBreadcrumb)
                .build();
    }

    public SpecimenListPanel createSpecimenListPanel(PanelBreadcrumb currentBreadcrumb) {
        return new SpecimenListPanel.Builder(specimenListPanel)
                .breadcrumb(currentBreadcrumb)
                .build();
    }

    public WelcomePanel createWelcomePanel() {
        WelcomePanel wp = welcomePanelProvider.getObject();
        wp.setBreadcrumb(new PanelBreadcrumb());
        return wp;
    }

}