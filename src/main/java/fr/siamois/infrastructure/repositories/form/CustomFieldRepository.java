package fr.siamois.infrastructure.repositories.form;

import fr.siamois.domain.models.form.customfield.CustomField;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomFieldRepository extends CrudRepository<CustomField, Long> {

    @Query(
            nativeQuery = true,
            value = """
            SELECT DISTINCT cfield.*
            FROM recording_unit ru
            JOIN action_unit au ON ru.fk_action_unit_id = au.action_unit_id
            JOIN spatial_unit su ON au.fk_spatial_unit_id = su.spatial_unit_id
            JOIN custom_form_response cfr ON ru.fk_custom_form_response = cfr.custom_form_response_id
            JOIN custom_form cf ON cfr.fk_custom_form_id = cf.form_id
            JOIN LATERAL jsonb_array_elements(cf.layout) AS layout_item ON TRUE
            JOIN LATERAL jsonb_array_elements(layout_item->'fields') AS field_id ON TRUE
            JOIN custom_field cfield ON cfield.custom_field_id = (field_id)::int
            WHERE su.spatial_unit_id = :spatialUnitId
        """
    )
    List<CustomField> findAllFieldsBySpatialUnitId(@Param("spatialUnitId") Long spatialUnitId);


}
