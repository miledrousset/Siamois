package fr.siamois.infrastructure.repositories.form;

import fr.siamois.domain.models.form.customformresponse.CustomFormResponse;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomFormResponseRepository extends CrudRepository<CustomFormResponse, Long> {



}
