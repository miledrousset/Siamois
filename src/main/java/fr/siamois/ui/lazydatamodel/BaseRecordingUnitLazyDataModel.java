package fr.siamois.ui.lazydatamodel;


import fr.siamois.domain.models.recordingunit.RecordingUnit;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public abstract class BaseRecordingUnitLazyDataModel extends BaseLazyDataModel<RecordingUnit> {

    private static final Map<String, String> FIELD_MAPPING;

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

}
