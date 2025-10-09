package fr.siamois.ui.bean.panel.models.panel.single.tab;

import fr.siamois.ui.lazydatamodel.BaseSpecimenLazyDataModel;
import lombok.Data;

@Data
public class SpecimenTab extends PanelTab {

    // Linked specimen
    private BaseSpecimenLazyDataModel specimenListLazyDataModel ;

    public SpecimenTab(String titleCode, String icon, String id, String root, BaseSpecimenLazyDataModel specimenListLazyDataModel) {
        super(titleCode, icon, id, root);
        this.specimenListLazyDataModel = specimenListLazyDataModel;
    }

}
