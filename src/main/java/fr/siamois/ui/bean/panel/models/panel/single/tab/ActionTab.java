package fr.siamois.ui.bean.panel.models.panel.single.tab;

import fr.siamois.ui.lazydatamodel.BaseActionUnitLazyDataModel;
import fr.siamois.ui.lazydatamodel.BaseSpecimenLazyDataModel;
import lombok.Data;

@Data
public class ActionTab extends PanelTab {

    // Linked specimen
    private BaseActionUnitLazyDataModel actionListLazyDataModel ;

    public ActionTab(String titleCode, String icon, String id, String root, BaseActionUnitLazyDataModel actionListLazyDataModel ) {
        super(titleCode, icon, id, root);
        this.actionListLazyDataModel = actionListLazyDataModel;
    }

}
