package fr.siamois.ui.bean.panel.models;

import fr.siamois.domain.models.spatialunit.SpatialUnit;
import lombok.Data;
import lombok.Getter;
import org.primefaces.model.menu.BaseMenuModel;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.MenuModel;

import java.io.Serializable;

@Getter
@Data
public class PanelBreadcrumb implements Serializable {

    private transient MenuModel model;

    public PanelBreadcrumb() {
        createMenu();
    }

    private void createMenu()
    {
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
        model.getElements().add(item);
    }
}