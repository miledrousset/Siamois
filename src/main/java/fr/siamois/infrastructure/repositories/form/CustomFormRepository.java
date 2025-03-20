package fr.siamois.infrastructure.repositories.form;

import fr.siamois.domain.models.form.customform.CustomForm;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomFormRepository extends CrudRepository<CustomForm, Long> {



}
