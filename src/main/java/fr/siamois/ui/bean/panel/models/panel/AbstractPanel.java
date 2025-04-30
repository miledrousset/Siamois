package fr.siamois.ui.bean.panel.models.panel;

import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import lombok.Data;

import java.io.Serializable;

@Data
public abstract class AbstractPanel implements Serializable {


    protected String title;
    protected String panelClass;
    protected String icon;
    protected PanelBreadcrumb breadcrumb;
    protected Boolean isBreadcrumbVisible = true;
    protected Boolean collapsed = false;

    protected AbstractPanel() {
    }

    protected AbstractPanel(String title, String icon, String panelClass) {
        this.title = title;
        this.icon = icon;
        this.panelClass = panelClass;
    }

    public abstract String display();


}