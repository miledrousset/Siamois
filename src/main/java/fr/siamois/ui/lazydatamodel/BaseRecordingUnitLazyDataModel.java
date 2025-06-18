package fr.siamois.ui.lazydatamodel;


import fr.siamois.domain.models.form.customfield.CustomFieldSelectOneFromFieldCode;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;

@Getter
@Setter
public abstract class BaseRecordingUnitLazyDataModel extends BaseLazyDataModel<RecordingUnit> {

    private static final Map<String, String> FIELD_MAPPING;

    // Fields definition for cell/bulk edit
    CustomFieldSelectOneFromFieldCode typeField = new CustomFieldSelectOneFromFieldCode();

    BaseRecordingUnitLazyDataModel() {
        typeField.setFieldCode("SIARU.TYPE");
    }

    static {
        Map<String, String> map = new HashMap<>();
        map.put("category", "c_label");
        map.put("creationTime", "creation_time");
        map.put("author", "p_lastname");
        FIELD_MAPPING = Collections.unmodifiableMap(map); // Ensure immutability
    }

    @Override
    protected Page<RecordingUnit> loadData(String name, Long[] categoryIds, Long[] personIds, String globalFilter, Pageable pageable) {
        return loadRecordingUnits(name, categoryIds, personIds, globalFilter, pageable);
    }

    protected abstract Page<RecordingUnit> loadRecordingUnits(
            String nameFilter, Long[] categoryIds, Long[] personIds,
            String globalFilter, Pageable pageable);

    @Override
    protected String getDefaultSortField() {
        return "recording_unit_id";
    }

    @Override
    protected Map<String, String> getFieldMapping() {
        return FIELD_MAPPING;
    }

    @Override
    public String getRowKey(RecordingUnit recordingUnit) {
        return recordingUnit != null ? Long.toString(recordingUnit.getId()) : null;
    }





    @Override
    public RecordingUnit getRowData(String rowKey) {
        List<RecordingUnit> units = getWrappedData();
        Long value = Long.valueOf(rowKey);

        for (RecordingUnit unit : units) {
            if (unit.getId().equals(value)) {
                return unit;
            }
        }

        return null;
    }
}
