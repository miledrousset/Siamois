package fr.siamois.ui.bean.panel.models.panel.single.tab;

import fr.siamois.ui.lazydatamodel.BaseLazyDataModel;
import fr.siamois.ui.lazydatamodel.BaseRecordingUnitLazyDataModel;
import lombok.Data;

@Data
public abstract class EntityListTab<T> extends PanelTab {


    private BaseLazyDataModel<T> lazyDataModel ;
    private Integer totalCount;

    protected EntityListTab(String titleCode, String icon, String id,
                         BaseLazyDataModel<T> lazyDataModel,
                         Integer totalCount) {
        super(titleCode, icon, id);
        this.lazyDataModel = lazyDataModel;
        this.totalCount = totalCount;
    }

}
