package fr.siamois.ui.bean.panel.models.panel.single.tab;

import lombok.Data;

@Data
public class FormPanelTab extends PanelTab {

    public FormPanelTab(String titleCode, String icon, String id, String root) {
        super(titleCode, icon, id, root);
    }
}
