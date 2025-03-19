package fr.siamois.domain.models.form.customFormResponse;
import fr.siamois.domain.models.form.customForm.CustomForm;
import fr.siamois.domain.models.form.customField.CustomField;
import fr.siamois.domain.models.form.customFieldAnswer.CustomFieldAnswer;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Entity
@Data
@Table(name = "custom_form_response")
public class CustomFormResponse {


    @Id
    @Column(name="custom_form_response_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="fk_custom_form_id")
    private CustomForm form;

    @OneToMany(
            cascade={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE},
            orphanRemoval = true,
            mappedBy = "fk.formResponse"
    )
    @MapKey(name="pk.field")
    private Map<CustomField, CustomFieldAnswer> answers = new HashMap<>();

    // Keep entities in sync
    public void addAnswer(CustomFieldAnswer answer) {
        this.answers.put(answer.getPk().getField(), answer);
        answer.getPk().setFormResponse(this);
    }

    public void removeAnswer(CustomFieldAnswer answer) {
        answer.getPk().setFormResponse(null);
        this.answers.remove(answer.getPk().getField());
    }

}
