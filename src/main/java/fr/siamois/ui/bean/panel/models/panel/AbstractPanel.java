package fr.siamois.ui.bean.panel.models.panel;

import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public abstract class AbstractPanel implements Serializable {


    protected String title;
    protected String panelClass;
    protected String icon;
    protected PanelBreadcrumb breadcrumb;
    protected Boolean isBreadcrumbVisible = true;
    protected Boolean collapsed = false;

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