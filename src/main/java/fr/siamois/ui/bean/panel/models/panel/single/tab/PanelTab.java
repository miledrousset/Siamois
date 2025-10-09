package fr.siamois.ui.bean.panel.models.panel.single.tab;

import lombok.Data;

@Data
public class PanelTab {

    private String titleCode;
    private String icon;
    private String id;
    private String root;

    public PanelTab(String titleCode, String icon, String id, String root) {
        this.titleCode = titleCode;
        this.icon = icon;
        this.id = id;
        this.root = root;
    }
}
