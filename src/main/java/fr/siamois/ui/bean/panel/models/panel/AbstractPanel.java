package fr.siamois.ui.bean.panel.models.panel;

import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import lombok.Data;

@Data
public abstract class AbstractPanel {


    private String title;
    private Object content;
    private String panelClass;
    private String icon;
    private PanelBreadcrumb breadcrumb;

    protected AbstractPanel() {
    }

    protected AbstractPanel(String title, String icon, String panelClass) {
        this.title = title;
        this.icon = icon;
        this.panelClass = panelClass;
    }

    public abstract String display();


}