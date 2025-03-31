package fr.siamois.ui.bean.panel.models.panel;

import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.panel.FlowBean;
import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.model.menu.DefaultMenuItem;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class NewRecordingUnitPanel extends AbstractPanel {

    // Deps
    private final LangBean langBean;
    private final SessionSettingsBean sessionSettingsBean;
    private final transient SpatialUnitService spatialUnitService;
    private final transient ActionUnitService actionUnitService;
    private final FlowBean flowBean;

    // Locals
    RecordingUnit recordingUnit;
    Long actionUnitId;


    public NewRecordingUnitPanel(LangBean langBean, SessionSettingsBean sessionSettingsBean, SpatialUnitService spatialUnitService, ActionUnitService actionUnitService, FlowBean flowBean) {
        super("Nouvelle unité d'enregistrement", "bi bi-pencil-square", "siamois-panel recording-unit-panel new-recording-unit-panel");
        this.langBean = langBean;
        this.sessionSettingsBean = sessionSettingsBean;
        this.spatialUnitService = spatialUnitService;
        this.actionUnitService = actionUnitService;
        this.flowBean = flowBean;
    }

    @Override
    public String display() {
        return "/panel/newRecordingUnitPanel.xhtml";
    }

    void init() {
        recordingUnit = new RecordingUnit();
        recordingUnit.setActionUnit(actionUnitService.findById(actionUnitId));
        recordingUnit.setCreatedByInstitution(recordingUnit.getActionUnit().getCreatedByInstitution());
        DefaultMenuItem item = DefaultMenuItem.builder()
                .value("Nouvelle unité d'enregistrement")
                .icon("bi bi-pencil-square")
                .build();
        this.getBreadcrumb().getModel().getElements().add(item);
    }

    public static class NewRecordingUnitPanelBuilder {

        private final NewRecordingUnitPanel newRecordingUnitPanel;

        public NewRecordingUnitPanelBuilder(ObjectProvider<NewRecordingUnitPanel> newRecordingUnitPanelProvider) {
            this.newRecordingUnitPanel = newRecordingUnitPanelProvider.getObject();
        }

        public NewRecordingUnitPanel.NewRecordingUnitPanelBuilder breadcrumb(PanelBreadcrumb breadcrumb) {
            newRecordingUnitPanel.setBreadcrumb(breadcrumb);

            return this;
        }

        public NewRecordingUnitPanel.NewRecordingUnitPanelBuilder actionUnitId(Long id) {
            newRecordingUnitPanel.setActionUnitId(id);

            return this;
        }

        public NewRecordingUnitPanel build() {
            newRecordingUnitPanel.init();
            return newRecordingUnitPanel;
        }
    }
}
