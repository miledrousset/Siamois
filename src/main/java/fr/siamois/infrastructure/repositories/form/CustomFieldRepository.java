package fr.siamois.infrastructure.repositories.form;

import fr.siamois.domain.models.form.customfield.CustomField;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomFieldRepository extends CrudRepository<CustomField, Long> {



}
