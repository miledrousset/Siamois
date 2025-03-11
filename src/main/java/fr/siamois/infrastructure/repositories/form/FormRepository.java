package fr.siamois.infrastructure.repositories.form;

import fr.siamois.domain.models.form.Form;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FormRepository extends CrudRepository<Form, Long> {

    Optional<Form> findById(Form form);

}
