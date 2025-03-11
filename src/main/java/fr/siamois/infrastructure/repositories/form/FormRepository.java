package fr.siamois.infrastructure.repositories.form;

import fr.siamois.domain.models.form.Form;
import fr.siamois.domain.models.form.FormQuestion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormRepository extends CrudRepository<Form, Long> {

    Optional<Form> findById(Form form);

}
