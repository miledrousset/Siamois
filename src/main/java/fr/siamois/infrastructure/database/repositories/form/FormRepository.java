package fr.siamois.infrastructure.database.repositories.form;

import fr.siamois.domain.models.form.customform.CustomForm;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FormRepository extends CrudRepository<CustomForm, Long> {

    Optional<CustomForm> findById(CustomForm form);

    @Query(
            value = """
            WITH candidates AS (
              SELECT f.*
              FROM form_scopes fs
              JOIN custom_form f ON f.form_id = fs.fk_custom_form_id
              WHERE fs.scope_level = 'ORG_WIDE'
                AND fs.fk_type_id = :conceptId
                AND fs.fk_institution_id = :institutionId

              UNION ALL

              SELECT f.*
              FROM form_scopes fs
              JOIN custom_form f ON f.form_id = fs.fk_custom_form_id
              WHERE fs.scope_level = 'GLOBAL_DEFAULT'
                AND fs.fk_type_id = :conceptId
            )
            SELECT *
            FROM candidates
            LIMIT 1
            """,
            nativeQuery = true
    )
    Optional<CustomForm> findEffectiveFormByTypeAndInstitution(
            @Param("conceptId") Long conceptId,
            @Param("institutionId") Long institutionId
    );

}
