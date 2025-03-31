package fr.siamois.ui.bean.panel.models.panel;

import fr.siamois.domain.models.exceptions.spatialunit.SpatialUnitAlreadyExistsException;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.utils.MessageUtils;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.panel.FlowBean;
import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class NewSpatialUnitPanel extends AbstractPanel {

    // Deps
    private final LangBean langBean;
    private final SessionSettingsBean sessionSettingsBean;
    private final transient SpatialUnitService spatialUnitService;
    private final FlowBean flowBean;

    // Locals
    SpatialUnit spatialUnit;


    public NewSpatialUnitPanel(LangBean langBean, SessionSettingsBean sessionSettingsBean, SpatialUnitService spatialUnitService, FlowBean flowBean) {
        super("Nouvelle unité spatiale", "bi bi-geo-alt", "siamois-panel spatial-unit-panel new-spatial-unit-panel");
        this.langBean = langBean;
        this.sessionSettingsBean = sessionSettingsBean;
        this.spatialUnitService = spatialUnitService;
        this.flowBean = flowBean;
    }

    @Override
    public String display() {
        return "/panel/newSpatialUnitPanel.xhtml";
    }

    void init() {
        spatialUnit = new SpatialUnit();
    }

    /**
     * Save the spatial unit in the database.
     * Display a message if the spatial unit already exists.
     * Display a message if the spatial unit has been created.
     *
     * @throws IllegalStateException if the collections are not defined
     */
    public boolean save() {

        try {
            SpatialUnit saved = spatialUnitService.save(sessionSettingsBean.getUserInfo(), spatialUnit.getName(), spatialUnit.getCategory(), Collections.emptyList());

            MessageUtils.displayInfoMessage(langBean, "spatialunit.created", saved.getName());

            flowBean.goToSpatialUnitByIdCurrentPanel(saved.getId(), this);

            return true;
        } catch (SpatialUnitAlreadyExistsException e) {
            log.error(e.getMessage(), e);
            MessageUtils.displayErrorMessage(langBean, "commons.error.spatialunit.alreadyexist", spatialUnit.getName());
            return false;
        }
    }

    public static class NewSpatialUnitPanelBuilder {

        private final NewSpatialUnitPanel newSpatialUnitPanel;

        public NewSpatialUnitPanelBuilder(ObjectProvider<NewSpatialUnitPanel> newSpatialUnitPanelProvider) {
            this.newSpatialUnitPanel = newSpatialUnitPanelProvider.getObject();
        }

        public NewSpatialUnitPanel.NewSpatialUnitPanelBuilder breadcrumb(PanelBreadcrumb breadcrumb) {
            newSpatialUnitPanel.setBreadcrumb(breadcrumb);

            return this;
        }

        public NewSpatialUnitPanel build() {
            newSpatialUnitPanel.init();
            return newSpatialUnitPanel;
        }
    }
}
