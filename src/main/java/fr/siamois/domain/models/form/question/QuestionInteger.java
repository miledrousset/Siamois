package fr.siamois.domain.models.form.question;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import com.vladmihalcea.*;
import lombok.Data;
import org.hibernate.annotations.Type;


@Data
@Entity
@Table(name = "question")
public class QuestionInteger extends Question {

    @Column(name = "options", columnDefinition = "jsonb")
    protected IntegerOptions options;

}
