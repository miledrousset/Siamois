package fr.siamois.infrastructure.database.repositories.form;

import fr.siamois.domain.models.form.customform.CustomForm;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FormRepository extends CrudRepository<CustomForm, Long> {

    Optional<CustomForm> findById(CustomForm form);

}
