package fr.siamois.ui.bean.panel.models.panel.single.tab;

import fr.siamois.ui.lazydatamodel.BaseLazyDataModel;
import fr.siamois.ui.lazydatamodel.BaseRecordingUnitLazyDataModel;
import lombok.Data;

@Data
public abstract class EntityListTab<T> extends PanelTab {


    private BaseLazyDataModel<T> lazyDataModel ;
    private Integer totalCount;

    protected EntityListTab(String titleCode, String icon, String id, String root,
                         BaseLazyDataModel<T> lazyDataModel,
                         Integer totalCount) {
        super(titleCode, icon, id, root);
        this.lazyDataModel = lazyDataModel;
        this.totalCount = totalCount;
    }

}
