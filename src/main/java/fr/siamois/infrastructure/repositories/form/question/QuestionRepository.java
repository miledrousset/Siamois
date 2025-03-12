package fr.siamois.infrastructure.repositories.form.question;



import fr.siamois.domain.models.form.customField.CustomField;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends CrudRepository<CustomField, Long> {


}
