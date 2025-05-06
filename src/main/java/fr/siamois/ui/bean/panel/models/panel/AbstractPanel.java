package fr.siamois.ui.bean.panel.models.panel;

import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

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

    public String formatUtcDateTime(OffsetDateTime dateTime) {
        if (dateTime == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneOffset.UTC);
        return formatter.format(dateTime);
    }

    protected AbstractPanel(String title, String icon, String panelClass) {
        this.title = title;
        this.icon = icon;
        this.panelClass = panelClass;
    }

    public abstract String display();

    public String displayHeader() {
        return null;
    }

}