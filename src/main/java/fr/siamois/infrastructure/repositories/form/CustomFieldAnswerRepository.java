package fr.siamois.infrastructure.repositories.form;

import fr.siamois.domain.models.form.customField.CustomField;
import fr.siamois.domain.models.form.customFieldAnswer.CustomFieldAnswer;
import fr.siamois.domain.models.form.customFieldAnswer.CustomFieldAnswerId;
import fr.siamois.domain.models.form.customFormResponse.CustomFormResponse;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomFieldAnswerRepository extends CrudRepository<CustomFieldAnswer, Long> {

    @Query(value = """
        SELECT * FROM custom_field_answer
        WHERE fk_form_response = :formResponseId
        AND fk_field_id = :fieldId
    """, nativeQuery = true)
    Optional<CustomFieldAnswer> findByFormResponseIdAndFieldId(
            @Param("formResponseId") Long formResponseId,
            @Param("fieldId") Long fieldId
    );

}
