package fr.siamois.domain.services.form;

import fr.siamois.domain.models.form.customfield.CustomField;
import fr.siamois.infrastructure.repositories.form.CustomFieldRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomFieldService {

    private final CustomFieldRepository customFieldRepository;

    public CustomFieldService(CustomFieldRepository customFieldRepository) {
        this.customFieldRepository = customFieldRepository;
    }

    // Find all the custom fields used in the recording units in the scope of a spatial unit
    public List<CustomField> findAllFieldsBySpatialUnitId(Long spatialUnitId) {
        return customFieldRepository.findAllFieldsBySpatialUnitId(spatialUnitId);
    }

}
