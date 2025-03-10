package fr.siamois.domain.models.form.question;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "question")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "answer_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id", nullable = false)
    private Long id;

    @Column(name = "label")
    protected String label;

    @Column(name = "hint")
    protected String hint;

    @Column(name = "answer_type")
    protected String answerType;

}
