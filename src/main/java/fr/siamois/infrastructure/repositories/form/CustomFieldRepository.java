package fr.siamois.infrastructure.repositories.form;

import fr.siamois.domain.models.form.CustomForm;
import fr.siamois.domain.models.form.customField.CustomField;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomFieldRepository extends CrudRepository<CustomField, Long> {



}
