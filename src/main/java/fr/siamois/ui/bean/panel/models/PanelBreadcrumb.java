package fr.siamois.ui.bean.panel.models;

import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.ui.bean.panel.models.panel.AbstractPanel;
import lombok.Data;
import lombok.Getter;
import org.primefaces.model.menu.BaseMenuModel;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

@Getter
@Data
public class PanelBreadcrumb  {

    private MenuModel model;
    private AbstractPanel panel;

    public PanelBreadcrumb(AbstractPanel panel) {
        createMenu(panel);
    }

    private void createMenu(AbstractPanel panel)
    {
        this.panel = panel;
        model = new BaseMenuModel();
        model.getElements().clear();

        // Home Item
        DefaultMenuItem item = DefaultMenuItem.builder()
                .value("Dashboard")
                .id("home")
                .icon("pi pi-home")
                .command("#{flowBean.goToHomeCurrentPanel}")
                .update("flow")
                .build();
        item.setParam("panel", panel);
        model.getElements().add(item);
    }


    public void addSpatialUnit(SpatialUnit spatialUnit) {
        DefaultMenuItem item = DefaultMenuItem.builder()
                .value(spatialUnit.getName())
                .id("site-item-1")
                .icon("pi pi-map-marker")
                .command("#{flowBean.goToHomeCurrentPanel}")
                .update("flow")
                .build();

        item.setParam("panel", panel);
        model.getElements().add(item);
    }
}