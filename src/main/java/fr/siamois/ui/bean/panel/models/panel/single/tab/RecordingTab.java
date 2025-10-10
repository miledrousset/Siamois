package fr.siamois.ui.bean.panel.models.panel.single.tab;

import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.ui.lazydatamodel.BaseActionUnitLazyDataModel;
import fr.siamois.ui.lazydatamodel.BaseRecordingUnitLazyDataModel;
import lombok.Data;

@Data
public class RecordingTab extends EntityListTab<RecordingUnit> {

    public RecordingTab(String titleCode, String icon, String id, String root,
                        BaseRecordingUnitLazyDataModel recordingListLazyDataModel, Integer count ) {
        super(titleCode, icon, id, root, recordingListLazyDataModel, count);
    }

}
