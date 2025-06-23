package fr.siamois.ui.lazydatamodel;


import fr.siamois.domain.models.form.customfield.CustomFieldSelectOneFromFieldCode;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.specimen.Specimen;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public abstract class BaseSpecimenLazyDataModel extends BaseLazyDataModel<Specimen> {

    private static final Map<String, String> FIELD_MAPPING;

    // Fields definition for cell/bulk edit
    CustomFieldSelectOneFromFieldCode typeField = new CustomFieldSelectOneFromFieldCode();

    BaseSpecimenLazyDataModel() {
        typeField.setFieldCode("SIAS.CATEGORY");
    }

    static {
        Map<String, String> map = new HashMap<>();
        map.put("category", "c_label");
        map.put("creationTime", "creation_time");
        map.put("author", "p_lastname");
        FIELD_MAPPING = Collections.unmodifiableMap(map); // Ensure immutability
    }

    @Override
    protected Page<Specimen> loadData(String name, Long[] categoryIds, Long[] personIds, String globalFilter, Pageable pageable) {
        return loadSpecimens(name, categoryIds, personIds, globalFilter, pageable);
    }

    protected abstract Page<Specimen> loadSpecimens(
            String nameFilter, Long[] categoryIds, Long[] personIds,
            String globalFilter, Pageable pageable);

    @Override
    protected String getDefaultSortField() {
        return "specimen_id";
    }

    @Override
    protected Map<String, String> getFieldMapping() {
        return FIELD_MAPPING;
    }

    @Override
    public String getRowKey(Specimen specimen) {
        return specimen != null ? Long.toString(specimen.getId()) : null;
    }


    @Override
    public Specimen getRowData(String rowKey) {
        List<Specimen> units = getWrappedData();
        Long value = Long.valueOf(rowKey);

        for (Specimen unit : units) {
            if (unit.getId().equals(value)) {
                return unit;
            }
        }

        return null;
    }
}
