package fr.siamois.ui.bean.panel.models.panel.single.tab;

import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.ui.lazydatamodel.BaseActionUnitLazyDataModel;
import lombok.Data;

@Data
public class ActionTab extends EntityListTab<ActionUnit> {


    public ActionTab(String titleCode, String icon, String id, BaseActionUnitLazyDataModel actionListLazyDataModel,
                     Integer count) {
        super(titleCode, icon, id, actionListLazyDataModel, count);
    }

}
