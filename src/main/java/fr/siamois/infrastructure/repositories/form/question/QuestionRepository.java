package fr.siamois.infrastructure.repositories.form.question;



import fr.siamois.domain.models.form.question.Question;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends CrudRepository<Question, Long> {


}
