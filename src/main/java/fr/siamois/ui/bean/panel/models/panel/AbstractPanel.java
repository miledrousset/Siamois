package fr.siamois.ui.bean.panel.models.panel;

import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import lombok.Data;
import lombok.Getter;

import java.awt.*;

@Data
public abstract class AbstractPanel {

    @Getter
    private String id;

    private String title;
    private String type;
    private Object content;
    private String icon;
    private PanelBreadcrumb breadcrumb;

    public AbstractPanel() {
    }

    public AbstractPanel(String id, String title, String type, String icon) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.icon = icon;
    }

    public abstract String display();


}