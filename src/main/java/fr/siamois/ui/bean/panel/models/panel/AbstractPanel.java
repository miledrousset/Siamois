package fr.siamois.ui.bean.panel.models.panel;

import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import lombok.Data;

import java.io.Serializable;

@Data
public abstract class AbstractPanel implements Serializable {


    private String title;
    private String panelClass;
    private String icon;
    private PanelBreadcrumb breadcrumb;
    private Boolean isBreadcrumbVisible = true;
    private Boolean collapsed = false;

    protected AbstractPanel() {
    }

    protected AbstractPanel(String title, String icon, String panelClass) {
        this.title = title;
        this.icon = icon;
        this.panelClass = panelClass;
    }

    public abstract String display();


}